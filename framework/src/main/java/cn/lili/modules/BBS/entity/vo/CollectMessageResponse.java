package cn.lili.modules.BBS.entity.vo;

import cn.lili.modules.member.entity.dos.Member;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-11-10 23:02:21
 **/
@Data
@ApiModel(value = "收藏了我消息列表分页")
public class CollectMessageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    private Long id;

    /**
     * 喜欢的帖子
     */
    private PostListResponse post;

    /**
     * 喜欢者信息
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
