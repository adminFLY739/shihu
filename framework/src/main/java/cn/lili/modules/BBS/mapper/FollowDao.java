/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 演示站点: https://www.linfeng.tech
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服  QQ: 3582996245
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.mapper;

import cn.lili.modules.BBS.entity.FollowEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-24 14:38:31
 */
@Mapper
public interface FollowDao extends BaseMapper<FollowEntity> {

	@Delete("delete from lf_follow where uid=#{uid} and follow_uid=#{followId}")
	void cancelFollow(@Param("uid")String uid, @Param("followId")String followId);
}
