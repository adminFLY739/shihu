package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuwenxin
 * @date 2023-11-13 23:14:52
 **/
@Data
@ApiModel(value = "邀请回答")
public class AddInviteForm {

    @ApiModelProperty(value = "邀请者id")
    private String uid;

    @ApiModelProperty(value = "话题id")
    private Integer discussId;
}
