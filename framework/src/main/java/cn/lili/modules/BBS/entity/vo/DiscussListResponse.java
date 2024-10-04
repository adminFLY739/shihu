package cn.lili.modules.BBS.entity.vo;

import cn.lili.modules.member.entity.dos.Member;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023/10/17 16:46:56
 */
@Data
public class DiscussListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 话题作者ID
     */
    private String uid;

    /**
     * 话题内容
     */
    private String description;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 评论量
     */
    private Integer postCount;

    /**
     * 浏览量
     */
    private Integer readCount;

    /**
     * 话题标签id
     */
    private String cut;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 置顶优先级
     */
    private Integer discussTop;

    /**
     * 分类名称
     */
    private List<String> cutName;

    /**
     * 话题人信息
     */
    private Member userInfo;

    /**
     * 关注量
     */
    private Integer followCount;

    /**
     * 用户等级
     */
    private Integer level;
}
