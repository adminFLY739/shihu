package cn.lili.modules.order.order.entity.dto;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.payment.entity.enums.PaymentMethodEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/14 13:42
 * @description: 退款日志搜索参数
 */

@Data
public class RefundLogSearchParams {

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "会员ID")
    private String memberId;

    /**
     * @see  PaymentMethodEnum
     */
    @ApiModelProperty(value = "退款方式")
    private String paymentName;

    @ApiModelProperty(value = "是否已退款")
    private Boolean isRefund;


    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "租户id")
    private String tenantId;



    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(orderSn)) {
            queryWrapper.like("order_sn", orderSn);
        }
        if (CharSequenceUtil.isNotEmpty(paymentName)) {
            queryWrapper.like("payment_name", paymentName);
        }
        //按买家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.MEMBER.name())) {
            queryWrapper.eq("me.member_id", UserContext.getCurrentUser().getId());
        }
        //按卖家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name())) {
            queryWrapper.eq("me.store_id", UserContext.getCurrentUser().getStoreId());
        }


        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }
        if(isRefund!=null){
            queryWrapper.eq("me.is_refund", isRefund);
        }

        //按时间查询
        if (StringUtils.isNotEmpty(startDate)) {
            queryWrapper.ge("me.create_time", DateUtil.parse(startDate));
        }
        if (StringUtils.isNotEmpty(endDate)) {
            queryWrapper.le("me.create_time", DateUtil.parse(endDate));
        }
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }
}
