/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 演示站点:https://www.linfeng.tech
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服  QQ: 3582996245
 *  Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-24 14:38:31
 */
@Data
@TableName("lf_follow")
public class FollowEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 关注的用户id
	 */
	private String followUid;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
