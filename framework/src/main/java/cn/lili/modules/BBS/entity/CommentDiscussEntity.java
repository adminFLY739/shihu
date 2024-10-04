package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("lf_comment_discuss")
public class CommentDiscussEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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

}
