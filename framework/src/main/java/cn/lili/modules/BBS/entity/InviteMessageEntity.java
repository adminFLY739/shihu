package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-13 16:40:38
 **/
@Data
@TableName("lf_invite_message")
public class InviteMessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 被邀请用户id
     */
    private String receiverUid;
    /**
     * 邀请人id
     */
    private String uid;
    /**
     * 话题id
     */
    private Integer discussId;
    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private Date createTime;
}