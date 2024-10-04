package cn.lili.modules.order.order.entity.dto;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.SearchVO;
import cn.lili.modules.order.order.entity.enums.DeliverStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.entity.enums.PayStatusEnum;
import cn.lili.modules.payment.entity.enums.PaymentMethodEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/14 11:30
 * @description: 收款交易查询参数
 */

@Data
public class PaymentLogSeacherParams  {

    @ApiModelProperty("订单编号")
    private String sn;

    @ApiModelProperty("交易编号 关联Trade")
    private String tradeSn;

    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * @see OrderStatusEnum
     */
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    /**
     * @see PayStatusEnum
     */
    @ApiModelProperty(value = "付款状态")
    private String payStatus;


    /**
     * @see  PaymentMethodEnum
     */
    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "支付时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;


    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(orderStatus)) {
            queryWrapper.eq("me.order_status", orderStatus);
        }
        if (CharSequenceUtil.isNotEmpty(payStatus)) {
            queryWrapper.eq("me.pay_status", payStatus);
        }
        if (CharSequenceUtil.isNotEmpty(sn)) {
            queryWrapper.like("me.sn", sn);
        }
        //按买家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.MEMBER.name())) {
            queryWrapper.eq("me.member_id", UserContext.getCurrentUser().getId());
        }
        //按卖家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name())) {
            queryWrapper.eq("me.store_id", UserContext.getCurrentUser().getStoreId());
        }

        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.MANAGER.name())
                && CharSequenceUtil.isNotEmpty(storeId)
        ) {
            queryWrapper.eq("me.store_id", storeId);
        }
        if (CharSequenceUtil.isNotEmpty(paymentMethod)) {
            queryWrapper.eq("me.payment_method", paymentMethod);
        }
        if (CharSequenceUtil.isNotEmpty(storeName)) {
            queryWrapper.like("me.store_name", storeName);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }

        //按时间查询
        if (StringUtils.isNotEmpty(startDate)) {
            queryWrapper.ge("me.create_time", DateUtil.parse(startDate));
        }
        if (StringUtils.isNotEmpty(endDate)) {
            queryWrapper.le("me.create_time", DateUtil.parse(endDate));
        }
        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }


}
