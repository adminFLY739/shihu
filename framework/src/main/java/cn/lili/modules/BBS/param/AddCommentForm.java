/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 演示站点:https://www.linfeng.tech
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服 QQ: 3582996245
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户添加评论")
public class AddCommentForm {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "pid")
    private Integer pid;

    @ApiModelProperty(value = "type")
    private Integer type;

    @ApiModelProperty(value = "toUid")
    private String toUid;

    @ApiModelProperty(value = "postId")
    private Long postId;

    @Length(max = 200, message = "评论内容不能超过200个字符")
    @NotBlank(message = "评论内容不能为空")
    @ApiModelProperty(value = "content")
    private String content;

    @ApiModelProperty(value = "receiverUid")
    private Long receiverUid;

}
