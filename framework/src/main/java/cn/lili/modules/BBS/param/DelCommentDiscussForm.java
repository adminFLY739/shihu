package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuwenxin
 * @date 2023-11-01 14:22:18
 **/
@Data
@ApiModel(value = "删除话题评论")
public class DelCommentDiscussForm {

    @ApiModelProperty(value = "id")
    private Long id;
}
