package cn.lili.modules.member.entity.dto;

import cn.hutool.core.date.DateUtil;
import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.member.entity.enums.MemberStatusEnum;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author: nxc
 * @since: 2023/7/4 10:42
 * @description: 用户租户搜索
 */

@Data
public class MemberTenantSearchParams extends PageVO implements Serializable {

    private static final long serialVersionUID = 2077146467396229607L;
    @ApiModelProperty(value = "会员id")
    private String memberId;

    /**
     * @see MemberStatusEnum
     */
    @ApiModelProperty(value = "用户状态")
    private String memberStatus;

    @ApiModelProperty(value = "会员名称")
    private String username;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "租户名称")
    private String name;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(username)) {
            queryWrapper.like("username",username);
        }
        if (StringUtils.isNotEmpty(memberId)) {
            queryWrapper.like("me.member_id", memberId);
        }
        if (StringUtils.isNotEmpty(memberStatus)) {
            queryWrapper.eq("member_status", memberStatus);
        }

        if (StringUtils.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }


        if (StringUtils.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }

        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like("name", name);
        }

        if (StringUtils.isNotEmpty(mobile)) {
            queryWrapper.like("mobile", mobile);
        }


        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");

        return queryWrapper;
    }

}
