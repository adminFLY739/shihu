package cn.lili.modules.BBS.entity.vo;

import cn.lili.modules.member.entity.dos.Member;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author wuwenxin
 * @date 2023-10-21 14:43:17
 **/
@Data
public class AppCommentDiscussResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 父级id
     */
    private Integer pid;

    /**
     * 评论人ID
     */
    private String uid;
    /**
     * 被回复用户ID
     */
    private String toUid;

    /**
     * 评论的话题ID
     */
    private Integer discussId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态
     * 0 下架  1正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子评论
     */
    private List<AppChildrenCommentDiscussResponse> children;

    /**
     * 评论用户信息
     */
    private Member userInfo;

    /**
     * 点赞数
     */
    private Integer thumbs;

    /**
     * 评论是否点赞
     */
    private Boolean isThumbs;
    /**
     * 用户等级
     */
    private Integer level;
}
