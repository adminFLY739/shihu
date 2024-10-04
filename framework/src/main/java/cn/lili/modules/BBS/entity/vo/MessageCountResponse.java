package cn.lili.modules.BBS.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuwenxin
 * @date 2023-11-09 21:38:22
 **/
@Data
public class MessageCountResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论转发未读消息数量
     */
    private Long commentMessageNoReadCount = 0L;
    /**
     * 赞同喜欢未读消息数量
     */
    private Long endorseMessageNoReadCount = 0L;
    /**
     * 收藏了我未读消息数量
     */
    private Long collectMessageNoReadCount = 0L;
    /**
     * 新增关注未读消息数量
     */
    private Long followMessageNoReadCount = 0L;
    /**
     * 邀请回答未读消息数量
     */
    private Long inviteMessageNoReadCount = 0L;
}
