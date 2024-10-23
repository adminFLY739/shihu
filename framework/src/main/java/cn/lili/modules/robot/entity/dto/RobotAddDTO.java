package cn.lili.modules.robot.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RobotAddDTO {
    @ApiModelProperty(value = "机器人昵称")
    private String nickName;

    public RobotAddDTO(RobotImportDTO memberImportDTO) {
        this.nickName = memberImportDTO.getNickName();
    }
}
