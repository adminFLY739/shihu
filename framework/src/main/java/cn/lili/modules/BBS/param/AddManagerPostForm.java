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

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


@Data
public class AddManagerPostForm implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * id
     */

    private Integer id;
    private String uid;

    /**
     * 圈子id
     */
//    @NotNull(message = "无参数圈子id")
    private Integer topicId;
    /**
     * 话题id
     */
    private Integer discussId;

    /**
     * 标题
     */
    @Length(max = 200, message = "标题不能超过200个字符")
    @NotBlank(message = "参数有误")
    private String title;
    /**
     * 内容
     */
    @Length(max = 400, message = "内容不能超过400个字符")
    @NotBlank(message = "参数有误")
    private String content;
    /**
     * 文件
     */
    private List<String> media;

    /**
     * 帖子类型：1 图文 ，2视频 ，3文章，4投票
     */
    @NotNull(message = "无参数type")
    private Integer type;
    /**
     * 地址名称
     */
    private String address;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 分类id
     */
    private List<Integer> cut;


}
