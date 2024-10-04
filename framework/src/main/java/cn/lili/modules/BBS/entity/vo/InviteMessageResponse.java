package cn.lili.modules.BBS.entity.vo;

import cn.lili.modules.member.entity.dos.Member;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-13 16:39:13
 **/
@Data
@ApiModel(value = "邀请回答消息列表分页")
public class InviteMessageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    private Long id;

    /**
     * 邀请回答的话题
     */
    private DiscussListResponse discuss;

    /**
     * 邀请人信息
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
