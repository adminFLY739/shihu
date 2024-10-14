package cn.lili.modules.robot.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotImportDTO {

    @ApiModelProperty(value = "机器人昵称")
    private String nickName;

    @ApiModelProperty(value = "会员用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

}
