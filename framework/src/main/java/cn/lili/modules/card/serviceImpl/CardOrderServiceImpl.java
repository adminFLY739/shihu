package cn.lili.modules.card.serviceImpl;

import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.event.TransactionCommitSendMQEvent;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.CardOrder;
import cn.lili.modules.card.entity.dos.Delivery;
import cn.lili.modules.card.entity.dto.CardOrderSearchParams;
import cn.lili.modules.card.entity.enums.deliveryStatus;
import cn.lili.modules.card.mapper.CardOrderMapper;
import cn.lili.modules.card.service.CardOrderService;
import cn.lili.modules.card.service.CardService;
import cn.lili.modules.card.service.DeliveryService;
import cn.lili.modules.order.order.entity.dto.OrderMessage;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.entity.enums.PayStatusEnum;
import cn.lili.modules.order.order.service.StoreFlowService;
import cn.lili.modules.order.trade.entity.dos.OrderLog;
import cn.lili.modules.order.trade.service.OrderLogService;
import cn.lili.modules.payment.entity.enums.PaymentMethodEnum;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.tags.OrderTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author: nxc
 * @since: 2023/6/27 09:51
 * @description: 卡券订单业务处理层
 */
@Service
@Slf4j
public class CardOrderServiceImpl extends ServiceImpl<CardOrderMapper, CardOrder> implements CardOrderService {

    @Autowired
    private CardService cardService;

    @Autowired
    private StoreFlowService storeFlowService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;


    @Autowired
    private DeliveryService deliveryService;

    @Override
    public CardOrder createCardOrder(String cardId) {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        Card card = cardService.getById(cardId);
        CardOrder cardOrder = new CardOrder(card);
        cardOrder.setSn(SnowFlake.createStr("C"));
        cardOrder.setOrderStatus(PayStatusEnum.UNPAID.name());
        cardOrder.setMemberId(tokenUser.getId());
        cardOrder.setMemberName(tokenUser.getUsername());
        this.save(cardOrder);
        return cardOrder;
    }

    @Override
    public CardOrder getBySn(String sn) {
        return this.getOne(new LambdaQueryWrapper<CardOrder>().eq(CardOrder::getSn, sn));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderSn, String paymentMethod, String receivableNo) {

        CardOrder cardOrder = this.getBySn(orderSn);
        //如果订单已支付，就不能再次进行支付
        if (cardOrder.getOrderStatus().equals(PayStatusEnum.PAID.name())) {
            log.error("订单[ {} ]检测到重复付款，请处理", orderSn);
            throw new ServiceException(ResultCode.PAY_DOUBLE_ERROR);
        }

        //修改订单状态
        cardOrder.setCompleteTime(new Date());
        cardOrder.setOrderStatus(PayStatusEnum.PAID.name());
        cardOrder.setReceivableNo(receivableNo);
        Delivery delivery = deliveryService.getOneByCardId(cardOrder.getCardId());
        cardOrder.setDeliveryCode(delivery.getDeliveryCode());
        deliveryService.updateMember(cardOrder.getMemberId(),delivery.getId());
        deliveryService.changeDeliveryStauts(delivery.getId(), deliveryStatus.NOTUSE.name());
        this.updateById(cardOrder);

        //记录店铺订单支付流水
        storeFlowService.payOrder(orderSn);

        //发送订单已付款消息
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderSn(cardOrder.getSn());
        orderMessage.setPaymentMethod(paymentMethod);
        orderMessage.setNewStatus(OrderStatusEnum.PAID);
        this.sendUpdateStatusMessage(orderMessage);

        String message = "订单付款，付款方式[" + PaymentMethodEnum.valueOf(paymentMethod).paymentName() + "]";
        OrderLog orderLog = new OrderLog(orderSn, "-1", UserEnums.SYSTEM.getRole(), "系统操作", message);
        orderLogService.save(orderLog);

    }

    @Override
    public IPage<CardOrder> queryCardOrder(CardOrderSearchParams queryParam, PageVO page) {
        AuthUser user = Objects.requireNonNull(UserContext.getCurrentUser());
        queryParam.setStoreId(user.getStoreId());
        return baseMapper.queryCardOrder(PageUtil.initPage(page),queryParam.queryWrapper());

    }

    @Transactional(rollbackFor = Exception.class)
    public void sendUpdateStatusMessage(OrderMessage orderMessage) {
        applicationEventPublisher.publishEvent(new TransactionCommitSendMQEvent("发送订单变更mq消息", rocketmqCustomProperties.getOrderTopic(), OrderTagsEnum.STATUS_CHANGE.name(), JSONUtil.toJsonStr(orderMessage)));
    }
}
