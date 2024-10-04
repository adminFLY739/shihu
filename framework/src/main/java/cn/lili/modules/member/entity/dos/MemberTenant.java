package cn.lili.modules.member.entity.dos;

import cn.lili.modules.member.entity.enums.MemberStatusEnum;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/7/4 09:25
 * @description: 用户租户管理
 */

@Data
@TableName("li_member_tenant")
@ApiModel(value = "用户租户关系")
public class MemberTenant extends BaseEntity {

    private static final long serialVersionUID = -4680260092546996026L;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * @see MemberStatusEnum
     */
    @ApiModelProperty(value = "用户状态")
    private String memberStatus;

    @ApiModelProperty(value = "用户id")
    private String memberId;

    @ApiModelProperty(value = "删除标志 true/false 是主租户/不是主租户")
    private Boolean mainTenant;



}
