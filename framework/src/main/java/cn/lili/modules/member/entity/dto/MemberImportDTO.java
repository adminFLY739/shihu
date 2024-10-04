package cn.lili.modules.member.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author: nxc
 * @since: 2023/7/6 09:01
 * @description: 用户导入DTO
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberImportDTO {

    @ApiModelProperty(value = "会员用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

}
