package cn.lili.modules.system.serviceimpl;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.OperationalJudgment;
import cn.lili.modules.logistics.LogisticsPluginFactory;
import cn.lili.modules.logistics.entity.dto.LabelOrderDTO;
import cn.lili.modules.member.service.StoreLogisticsService;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.entity.enums.DeliverStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.service.OrderItemService;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.modules.store.entity.dos.StoreLogistics;
import cn.lili.modules.store.entity.dto.StoreDeliverGoodsAddressDTO;
import cn.lili.modules.store.service.StoreDetailService;
import cn.lili.modules.system.entity.dos.Logistics;
import cn.lili.modules.system.entity.dto.LogisticsSearchParams;
import cn.lili.modules.system.entity.vo.Traces;
import cn.lili.modules.system.mapper.LogisticsMapper;
import cn.lili.modules.system.service.LogisticsService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物流公司业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 8:02 下午
 */
@Slf4j
@Service
public class LogisticsServiceImpl extends ServiceImpl<LogisticsMapper, Logistics> implements LogisticsService {
    /**
     * 物流抽象工厂
     */
    @Autowired
    private LogisticsPluginFactory logisticsPluginFactory;
    /**
     * 订单
     */
    @Autowired
    private OrderService orderService;
    /**
     * 子订单
     */
    @Autowired
    private OrderItemService orderItemService;
    /**
     * 店铺物流公司
     */
    @Autowired
    private StoreLogisticsService storeLogisticsService;
    /**
     * 店铺详情
     */
    @Autowired
    private StoreDetailService storeDetailService;

    @Override
    public Traces getLogisticTrack(String logisticsId, String logisticsNo, String phone,String tenantId) {
        try {
            return logisticsPluginFactory.filePlugin(tenantId).pollQuery(this.getById(logisticsId), logisticsNo, phone,tenantId);
        } catch (Exception e) {
            log.error("获取物流公司错误", e);

        }
        return null;
    }

    @Override
    public Traces getLogisticMapTrack(String logisticsId, String logisticsNo, String phone, String from, String to,String tenantId) {
        try {
            return logisticsPluginFactory.filePlugin(tenantId).pollMapTrack(this.getById(logisticsId), logisticsNo, phone, from, to);
        } catch (Exception e) {
            log.error("获取物流公司错误", e);

        }
        return null;
    }

    @Override
    public String labelOrder(String orderSn, String logisticsId) {

        //获取订单及子订单
        Order order = OperationalJudgment.judgment(orderService.getBySn(orderSn));
        if (order.getDeliverStatus().equals(DeliverStatusEnum.UNDELIVERED.name())
                && order.getOrderStatus().equals(OrderStatusEnum.UNDELIVERED.name())) {

            //订单货物
            List<OrderItem> orderItems = orderItemService.getByOrderSn(orderSn);
            //获取对应物流
            Logistics logistics = this.getById(logisticsId);
            // 店铺-物流公司设置
            LambdaQueryWrapper<StoreLogistics> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(StoreLogistics::getLogisticsId, logisticsId);
            lambdaQueryWrapper.eq(StoreLogistics::getStoreId, order.getStoreId());
            StoreLogistics storeLogistics = storeLogisticsService.getOne(lambdaQueryWrapper);
            //获取店家信息
            StoreDeliverGoodsAddressDTO storeDeliverGoodsAddressDTO = storeDetailService.getStoreDeliverGoodsAddressDto(order.getStoreId());

            LabelOrderDTO labelOrderDTO = new LabelOrderDTO();
            labelOrderDTO.setOrder(order);
            labelOrderDTO.setOrderItems(orderItems);
            labelOrderDTO.setLogistics(logistics);
            labelOrderDTO.setStoreLogistics(storeLogistics);
            labelOrderDTO.setStoreDeliverGoodsAddressDTO(storeDeliverGoodsAddressDTO);
            //触发电子面单
            return logisticsPluginFactory.filePlugin(order.getTenantId()).labelOrder(labelOrderDTO);
        } else {
            throw new ServiceException(ResultCode.ORDER_LABEL_ORDER_ERROR);
        }
    }

    @Override
    public List<Logistics> getOpenLogistics(String tenantId) {
        LambdaQueryWrapper<Logistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Logistics::getTenantId,tenantId);
        queryWrapper.eq(Logistics::getDisabled, SwitchEnum.OPEN.name());
        return this.list(queryWrapper);
    }

  @Override
  public IPage<Logistics> queryByParams(LogisticsSearchParams logisticsSearchParams) {
      return baseMapper.queryLogisticsList(PageUtil.initPage(logisticsSearchParams), logisticsSearchParams.queryWrapper());
  }

}
