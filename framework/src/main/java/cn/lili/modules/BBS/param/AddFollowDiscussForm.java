package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuwenxin
 * @date 2023-10-25 22:31:44
 **/
@Data
@ApiModel(value = "话题关注")
public class AddFollowDiscussForm {

    @ApiModelProperty(value = "话题id")
    private Integer discussId;
}
