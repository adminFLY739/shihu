package cn.lili.modules.member.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRealNameDTO {

    @ApiModelProperty(value = "会员id")
    @NotNull(message = "会员ID不能为空")
    private String id;

    @ApiModelProperty(value = "学号")
    @NotNull(message = "学号不能为空")
    private String username;

    @ApiModelProperty(value = "姓名")
    @NotNull(message = "姓名不能为空")
    private String studentId;
}
