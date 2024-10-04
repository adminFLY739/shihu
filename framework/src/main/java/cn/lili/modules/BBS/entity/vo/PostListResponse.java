/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 商业版演示站点: https://www.linfeng.tech
 * 商业版购买联系技术客服	 QQ:  3582996245
 * 可正常分享和学习源码，不得转卖或非法牟利！
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.entity.vo;


import cn.lili.modules.member.entity.dos.Member;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class PostListResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Integer id;
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 圈子id
	 */
	private Integer topicId;
	/**
	 * 话题id
	 */
	private Integer discussId;
	/**
	 * 投票id
	 */
	private Integer voteId;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 文件
	 */
//	private String media;
	List<String> media;
	/**
	 * 浏览量
	 */
	private Integer readCount;
	/**
	 * 赞同量
	 */
	private Integer thumbCount;
	/**
	 * 反对量
	 */
	private Integer opposeCount;
	/**
	 * 置顶
	 */
	private Integer postTop;
	/**
	 * 帖子类型：1 图文 ，2视频 ，3文章，4投票
	 */
	private Integer type;
	/**
	 * 地址名称
	 */
	private String address;
	/**
	 * 经度
	 */
	private Double longitude;
	/**
	 * 纬度
	 */
	private Double latitude;
	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 收藏数
	 */
	private Integer collectionCount;
	/**
	 * 评论数
	 */
	private Integer commentCount;

	/**
	 * 用户信息
	 */
	private Member userInfo;

	/**
	 * 话题
	 */
	private String discussTitle;

	/**
	 * 动态是否收藏
	 */
	private Boolean isCollection;

	/**
	 * 是否赞同帖子
	 */
	private Boolean isPostThumb;

	/**
	 * 是否反对帖子
	 */
	private Boolean isPostOppose;

	/**
	 * 投票
	 */
	private Object voteInfo;

	/**
	 * 圈子名称
	 */
	private String topicName;

	/**
	 * 分类id
	 */
	private List<Integer> cut;

	/**
	 * 分类名称
	 */
	private List<String> cutName;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 用户等级
	 */
	private Integer level;

}
