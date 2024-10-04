package cn.lili.modules.wallet.entity.vo;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 余额提现记录查询条件
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Data
@ApiModel(value = "余额提现记录查询条件")
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithdrawApplyQueryVO implements Serializable {


    private static final long serialVersionUID = 4735408873104054674L;

    /**
     * 充值订单编号
     */
    @ApiModelProperty(value = "充值订单编号")
    private String sn;

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
     * 提现申请状态
     */
    @ApiModelProperty(value = "提现申请状态")
    private String applyStatus;
    /**
     * 提现申请时间
     */
    @ApiModelProperty(value = "提现申请时间起始日期")
    private String startDate;
    /**
     * 提现申请时间
     */
    @ApiModelProperty(value = "提现申请时间结束日期")
    private String endDate;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("me.member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("me.member_id", memberId);
        }
        if (CharSequenceUtil.isNotEmpty(sn)) {
            queryWrapper.like("me.sn",sn);
        }
        if (CharSequenceUtil.isNotEmpty(applyStatus)) {
            queryWrapper.eq("me.apply_status",applyStatus);
        }
        if (CharSequenceUtil.isNotEmpty(startDate) && CharSequenceUtil.isNotEmpty(endDate)) {
            queryWrapper.between("me.pay_time", startDate, endDate);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("me.tenant_id", tenantId);
        }
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }




}
