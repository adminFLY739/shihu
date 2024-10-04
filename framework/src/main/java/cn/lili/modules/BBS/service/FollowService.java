/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服 QQ: 3582996245
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 演示站点:https://www.linfeng.tech
 * 版权所有，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.FollowEntity;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-24 14:38:31
 */
public interface FollowService extends IService<FollowEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer getFollowCount(String uid);

    Integer getFans(String uid);

    Boolean isFollowOrNot(String uid, String id);

    List<String> getFollowUid(String userId);

    List<String> getFollowUids(String uid);

    Integer isFollow(String uid,String followUid);

    List<String> getFansList(String uid);
}

