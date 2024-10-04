package cn.lili.modules.order.order.serviceimpl;

import cn.hutool.json.JSONUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.modules.member.entity.dos.MemberAddress;
import cn.lili.modules.member.entity.enums.PointTypeEnum;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.order.cart.entity.dto.MemberCouponDTO;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.enums.CartTypeEnum;
import cn.lili.modules.order.cart.entity.enums.DeliveryMethodEnum;
import cn.lili.modules.order.cart.entity.vo.CartVO;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dos.Trade;
import cn.lili.modules.order.order.entity.enums.PayStatusEnum;
import cn.lili.modules.order.order.mapper.TradeMapper;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.modules.order.order.service.TradeService;
import cn.lili.modules.promotion.service.CouponService;
import cn.lili.modules.promotion.service.KanjiaActivityService;
import cn.lili.modules.promotion.service.MemberCouponService;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.OrderTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 7:39 下午
 */
@Service
public class TradeServiceImpl extends ServiceImpl<TradeMapper, Trade> implements TradeService {

    /**
     * 缓存
     */
    @Autowired
    private Cache<Object> cache;
    /**
     * 订单
     */
    @Autowired
    private OrderService orderService;
    /**
     * 会员
     */
    @Autowired
    private MemberService memberService;
    /**
     * 优惠券
     */
    @Autowired
    private CouponService couponService;
    /**
     * 会员优惠券
     */
    @Autowired
    private MemberCouponService memberCouponService;
    /**
     * 砍价
     */
    @Autowired
    private KanjiaActivityService kanjiaActivityService;

    /**
     * RocketMQ
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Trade createTrade(TradeDTO tradeDTO) {

        //创建订单预校验
        createTradeCheck(tradeDTO);


        Trade trade = new Trade(tradeDTO);
        String key = CachePrefix.TRADE.getPrefix() + trade.getSn();
        //优惠券预处理
        couponPretreatment(tradeDTO);
        //积分预处理
        pointPretreatment(tradeDTO);
        //添加交易
        this.save(trade);
        //添加订单
        orderService.intoDB(tradeDTO);

        //砍价订单处理
        kanjiaPretreatment(tradeDTO);
        //写入缓存，给消费者调用
        cache.put(key, JSONUtil.toJsonStr(tradeDTO));
        //构建订单创建消息
        String destination = rocketmqCustomProperties.getOrderTopic() + ":" + OrderTagsEnum.ORDER_CREATE.name();
        //发送订单创建消息
        rocketMQTemplate.asyncSend(destination, key, RocketmqSendCallbackBuilder.commonCallback());
        return trade;
    }

    /**
     * 创建订单最后一步校验
     *
     * @param tradeDTO 购物车视图
     */
    private void createTradeCheck(TradeDTO tradeDTO) {

        if(tradeDTO.getStoreAddress() == null){
            //创建订单如果没有收获地址，
            MemberAddress memberAddress = tradeDTO.getMemberAddress();
            if (memberAddress == null) {
                throw new ServiceException(ResultCode.MEMBER_ADDRESS_NOT_EXIST);
            }
            //订单配送区域校验
            if (tradeDTO.getNotSupportFreight() != null && !tradeDTO.getNotSupportFreight().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder("包含商品有-");
                tradeDTO.getNotSupportFreight().forEach(sku -> stringBuilder.append(sku.getGoodsSku().getGoodsName()));
                throw new ServiceException(ResultCode.ORDER_NOT_SUPPORT_DISTRIBUTION, stringBuilder.toString());
            }

            if (tradeDTO.getCartList().stream().noneMatch(CartVO::getChecked)) {
                throw new ServiceException(ResultCode.ORDER_NOT_EXIST_VALID);
            }
            if(tradeDTO.getCartList().stream().allMatch(item -> item.getDeliveryMethod().equals(DeliveryMethodEnum.SELF_PICK_UP.name()))){
                throw new ServiceException(ResultCode.STORE_ADDRESS_NOT_EXIST);
            }
        }

    }

    @Override
    public Trade getBySn(String sn) {
        LambdaQueryWrapper<Trade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Trade::getSn, sn);
        return this.getOne(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payTrade(String tradeSn, String paymentName, String receivableNo) {
        LambdaQueryWrapper<Order> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(Order::getTradeSn, tradeSn);
        List<Order> orders = orderService.list(orderQueryWrapper);
        for (Order order : orders) {
            orderService.payOrder(order.getSn(), paymentName, receivableNo);
        }
        Trade trade = this.getBySn(tradeSn);
        trade.setPayStatus(PayStatusEnum.PAID.name());
        this.saveOrUpdate(trade);
    }

    /**
     * 优惠券预处理
     * 下单同时，扣除优惠券
     *
     * @param tradeDTO 购物车视图
     */
    private void couponPretreatment(TradeDTO tradeDTO) {
        List<MemberCouponDTO> memberCouponDTOList = new ArrayList<>();
        if (null != tradeDTO.getPlatformCoupon()) {
            memberCouponDTOList.add(tradeDTO.getPlatformCoupon());
        }
        Collection<MemberCouponDTO> storeCoupons = tradeDTO.getStoreCoupons().values();
        if (!storeCoupons.isEmpty()) {
            memberCouponDTOList.addAll(storeCoupons);
        }
        List<String> ids = memberCouponDTOList.stream().map(e -> e.getMemberCoupon().getId()).collect(Collectors.toList());
        memberCouponService.used(tradeDTO.getMemberId(), ids,tradeDTO.getTenantId());
        memberCouponDTOList.forEach(e -> couponService.usedCoupon(e.getMemberCoupon().getCouponId(), 1));

    }

    /**
     * 创建交易，积分处理
     *
     * @param tradeDTO 购物车视图
     */
    private void pointPretreatment(TradeDTO tradeDTO) {

        //需要支付积分
        if (tradeDTO.getPriceDetailDTO() != null
                && tradeDTO.getPriceDetailDTO().getPayPoint() != null
                && tradeDTO.getPriceDetailDTO().getPayPoint() > 0) {
            StringBuilder orderSns = new StringBuilder();
            for (CartVO item : tradeDTO.getCartList()) {
                orderSns.append(item.getSn());
            }
            boolean result = memberService.updateMemberPoint(tradeDTO.getPriceDetailDTO().getPayPoint(), PointTypeEnum.REDUCE.name(), tradeDTO.getMemberId(),
                    "订单【" + orderSns + "】创建，积分扣减");

            if (!result) {
                throw new ServiceException(ResultCode.PAY_POINT_ENOUGH);
            }
        }
    }

    /**
     * 创建交易、砍价处理
     *
     * @param tradeDTO 购物车视图
     */
    private void kanjiaPretreatment(TradeDTO tradeDTO) {
        if (tradeDTO.getCartTypeEnum().equals(CartTypeEnum.KANJIA)) {
            String kanjiaId = tradeDTO.getSkuList().get(0).getKanjiaId();
            kanjiaActivityService.endKanjiaActivity(kanjiaId);
        }
    }

}