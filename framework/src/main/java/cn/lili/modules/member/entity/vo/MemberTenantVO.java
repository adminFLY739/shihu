package cn.lili.modules.member.entity.vo;

import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.modules.member.entity.enums.MemberStatusEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/7/4 11:00
 * @description: 用户租户VO
 */

@Data
public class MemberTenantVO  {

    /**
     * @see MemberStatusEnum
     */
    @ApiModelProperty(value = "用户状态")
    private String memberStatus;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "用户id")
    private String memberId;

    @ApiModelProperty(value = "租户名称")
    private String name;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "删除标志 true/false 是主租户/不是主租户")
    private Boolean mainTenant;


    @ApiModelProperty(value = "手机号码", required = true)
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String mobile;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;


}
