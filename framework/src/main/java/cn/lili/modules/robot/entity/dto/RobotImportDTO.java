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
}
