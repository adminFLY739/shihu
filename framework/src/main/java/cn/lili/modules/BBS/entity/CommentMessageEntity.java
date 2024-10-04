package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wuwenxin
 * @date 2023-11-07 17:32:57
 **/
@Data
@TableName("lf_comment_message")
public class CommentMessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 被评论人id
     */
    private Long receiverUid;
    /**
     * 评论id
     */
    private Long commentId;
    /**
     * 是否已读
     */
    private Boolean isRead;
}
