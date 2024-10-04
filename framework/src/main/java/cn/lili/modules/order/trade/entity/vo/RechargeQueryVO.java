package cn.lili.modules.order.trade.entity.vo;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 预存款充值记录查询条件
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Data
@ApiModel(value = "预存款充值记录查询条件")
@AllArgsConstructor
@NoArgsConstructor
public class RechargeQueryVO implements Serializable {


    private static final long serialVersionUID = 318396158590640917L;

    /**
     * 充值订单编号
     */
    @ApiModelProperty(value = "充值订单编号")
    private String rechargeSn;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员Id")
    private String memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String memberName;
    /**
     * 充值时间
     */
    @ApiModelProperty(value = "充值开始时间")
    private String startDate;

    /**
     * 充值时间
     */
    @ApiModelProperty(value = "充值结束时间")
    private String endDate;


    @ApiModelProperty(value = "租户id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("member_id", memberId);
        }
        if (CharSequenceUtil.isNotEmpty(rechargeSn)) {
            queryWrapper.like("rechargeSn", rechargeSn);
        }
        if (CharSequenceUtil.isNotEmpty(startDate) && CharSequenceUtil.isNotEmpty(endDate)) {
            queryWrapper.between("pay_time", startDate, endDate);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }


}
