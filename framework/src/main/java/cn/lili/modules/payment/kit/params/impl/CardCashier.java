package cn.lili.modules.payment.kit.params.impl;

import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.card.entity.dos.CardOrder;
import cn.lili.modules.card.service.CardOrderService;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.entity.enums.PayStatusEnum;
import cn.lili.modules.payment.entity.enums.CashierEnum;
import cn.lili.modules.payment.kit.dto.PayParam;
import cn.lili.modules.payment.kit.dto.PaymentSuccessParams;
import cn.lili.modules.payment.kit.params.CashierExecute;
import cn.lili.modules.payment.kit.params.dto.CashierParam;
import cn.lili.modules.system.entity.dto.BaseSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 卡券支付信息获取
 *
 * @author Chopper
 * @since 2021-01-25 20:00
 */
@Slf4j
@Component
public class CardCashier implements CashierExecute {
    /**
     * 订单
     */
    @Autowired
    private CardOrderService cardOrderService;
    /**
     * 设置
     */
    @Autowired
    private SettingService settingService;

    @Override
    public CashierEnum cashierEnum() {
        return CashierEnum.CARD;
    }

    @Override
    public CashierParam getPaymentParams(PayParam payParam) {
        if (payParam.getOrderType().equals(CashierEnum.CARD.name())) {
            //准备返回的数据
            CashierParam cashierParam = new CashierParam();
            //订单信息获取
            CardOrder cardOrder = cardOrderService.getBySn(payParam.getSn());

            //如果订单已支付，则不能发器支付
            if (cardOrder.getOrderStatus().equals(PayStatusEnum.PAID.name())) {
                throw new ServiceException(ResultCode.PAY_DOUBLE_ERROR);
            }
            //如果订单状态不是待付款，则抛出异常
            if (!cardOrder.getOrderStatus().equals(OrderStatusEnum.UNPAID.name())) {
                throw new ServiceException(ResultCode.PAY_BAN);
            }
            cashierParam.setPrice(cardOrder.getPrice());

            try {
                BaseSetting baseSetting = JSONUtil.toBean(settingService.get(SettingEnum.BASE_SETTING.name()).getSettingValue(), BaseSetting.class);
                cashierParam.setTitle(baseSetting.getSiteName());
            } catch (Exception e) {
                cashierParam.setTitle("多用户商城，在线支付");
            }


            cashierParam.setDetail(cardOrder.getCardName());

            cashierParam.setOrderSns(payParam.getSn());
            cashierParam.setCreateTime(cardOrder.getCreateTime());
            return cashierParam;
        }

        return null;
    }

    @Override
    public void paymentSuccess(PaymentSuccessParams paymentSuccessParams) {

        PayParam payParam = paymentSuccessParams.getPayParam();
        if (payParam.getOrderType().equals(CashierEnum.CARD.name())) {
            cardOrderService.payOrder(payParam.getSn(),
                    paymentSuccessParams.getPaymentMethod(),
                    paymentSuccessParams.getReceivableNo());
            log.info("订单{}支付成功,金额{},方式{}", payParam.getSn(),
                    paymentSuccessParams.getPaymentMethod(),
                    paymentSuccessParams.getReceivableNo());
        }
    }

    @Override
    public Boolean paymentResult(PayParam payParam) {
        if (payParam.getOrderType().equals(CashierEnum.ORDER.name())) {
            CardOrder cardOrder = cardOrderService.getBySn(payParam.getSn());
            if (cardOrder != null) {
                return PayStatusEnum.PAID.name().equals(cardOrder.getOrderStatus());
            } else {
                throw new ServiceException(ResultCode.PAY_NOT_EXIST_ORDER);
            }
        }
        return false;
    }
}
