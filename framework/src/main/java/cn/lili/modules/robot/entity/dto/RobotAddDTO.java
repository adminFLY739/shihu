package cn.lili.modules.robot.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RobotAddDTO {
    @ApiModelProperty(value = "机器人昵称")
    private String nickName;

    @ApiModelProperty(value = "用户用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "租户id")
    private String tenantIds;

    public RobotAddDTO(RobotImportDTO memberImportDTO) {
        this.nickName = memberImportDTO.getNickName();
        this.username = memberImportDTO.getUsername();
        this.mobile = memberImportDTO.getMobile();
        this.password = memberImportDTO.getPassword();
        this.tenantIds = memberImportDTO.getTenantId();
    }

}
