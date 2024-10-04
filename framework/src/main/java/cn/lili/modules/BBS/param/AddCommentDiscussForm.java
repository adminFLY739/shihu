package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author wuwenxin
 * @date 2023-10-21 16:19:30
 **/
@Data
@ApiModel(value = "用户添加话题评论")
public class AddCommentDiscussForm {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "pid")
    private Integer pid;

    @ApiModelProperty(value = "toUid")
    private String toUid;

    @ApiModelProperty(value = "discussId")
    private Integer discussId;

    @Length(max = 200, message = "评论内容不能超过200个字符")
    @NotBlank(message = "评论内容不能为空")
    @ApiModelProperty(value = "content")
    private String content;
}
