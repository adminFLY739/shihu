package cn.lili.modules.BBS.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023/10/18 8:43:01
 */
@Data
public class DiscussDetailResponse implements Serializable {
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
    private List<Integer> cut;

    /**
     * 分类名称
     */
    private List<String> cutName;

    /**
     * 回答列表
     */
    private List<PostListResponse> postDetails;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 关注量
     */
    private Integer followCount;


    /**
     * 是否关注
     */
    private Boolean isFollowDiscuss;
}
