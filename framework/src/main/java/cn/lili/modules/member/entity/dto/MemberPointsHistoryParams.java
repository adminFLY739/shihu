package cn.lili.modules.member.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.common.vo.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: nxc
 * @since: 2023/6/14 09:04
 * @description: 用户积分历史VO,用于输出操作
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPointsHistoryParams extends PageVO {

    private static final long serialVersionUID = -5852286743031419575L;
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "会员ID")
    private String memberId;

    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @ApiModelProperty(value = "会员名称")
    private String memberName;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("me.member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("me.member_id", memberId);
        }

        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_ids", tenantId);
        }
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }

}
