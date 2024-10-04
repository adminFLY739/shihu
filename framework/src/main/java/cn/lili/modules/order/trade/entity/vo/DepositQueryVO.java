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
@ApiModel(value = "预存款变动记录查询条件")
@AllArgsConstructor
@NoArgsConstructor
public class DepositQueryVO implements Serializable {


    private static final long serialVersionUID = -6413611244037073693L;

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

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
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
        if (CharSequenceUtil.isNotEmpty(startDate) && CharSequenceUtil.isNotEmpty(endDate)) {
            queryWrapper.between("create_time", startDate, endDate);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }




}
