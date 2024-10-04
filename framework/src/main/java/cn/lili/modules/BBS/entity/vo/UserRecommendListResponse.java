package cn.lili.modules.BBS.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wuwenxin
 * @date 2023-11-13 14:09:22
 **/
@Data
@ApiModel(value = "推荐用户分页")
public class UserRecommendListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String id;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 是否已邀请
     */
    private Boolean isInvite;
    /**
     * 用户等级
     */
    private Integer level;
}
