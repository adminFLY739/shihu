package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-11 15:57:10
 **/
@Data
@TableName("lf_follow_message")
public class FollowMessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 被关注用户id
     */
    private String receiverUid;
    /**
     * 关注者id
     */
    private String uid;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private Date createTime;
}
