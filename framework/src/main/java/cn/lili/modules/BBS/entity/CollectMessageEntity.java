package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-10 23:00:13
 **/
@Data
@TableName("lf_collect_message")
public class CollectMessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 被赞同用户id
     */
    private String receiverUid;
    /**
     * 赞同者id
     */
    private String uid;
    /**
     * 帖子id
     */
    private Integer postId;
    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private Date createTime;
}
