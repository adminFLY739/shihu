package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuwenxin
 * @date 2023-11-10 15:35:43
 **/
@Data
@ApiModel(value = "帖子点赞")
public class AddPostThumbForm {
    @ApiModelProperty(value = "帖子id")
    private Integer id;

    @ApiModelProperty(value = "帖子的用户id")
    private String uid;
}
