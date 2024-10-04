package cn.lili.modules.BBS.entity.vo;

import cn.lili.modules.member.entity.dos.Member;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-07 20:41:55
 **/
@Data
@ApiModel(value = "评论消息列表分页")
public class CommentMessageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    private Long id;

    /**
     * 评论的帖子
     */
    private PostListResponse post;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者信息
     */
    private Member userInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否已读
     */
    private Boolean isRead;
    /**
     * 用户等级
     */
    private Integer level;
}
