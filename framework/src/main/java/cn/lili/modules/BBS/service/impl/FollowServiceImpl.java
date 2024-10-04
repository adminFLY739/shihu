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
package cn.lili.modules.BBS.service.impl;

import cn.lili.modules.BBS.entity.FollowEntity;
import cn.lili.modules.BBS.mapper.FollowDao;
import cn.lili.modules.BBS.service.FollowService;
import cn.lili.modules.BBS.utils.PageUtils;
import cn.lili.modules.BBS.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("followService")
public class FollowServiceImpl extends ServiceImpl<FollowDao, FollowEntity> implements FollowService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FollowEntity> page = this.page(
                new Query<FollowEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer getFollowCount(String uid) {
        return this.lambdaQuery()
                .eq(FollowEntity::getUid, uid)
                .count().intValue();
    }

    @Override
    public Integer getFans(String uid) {

        return this.lambdaQuery()
                .eq(FollowEntity::getFollowUid,uid)
                .count().intValue();
    }

    @Override
    public Boolean isFollowOrNot(String uid, String id) {
        LambdaQueryWrapper<FollowEntity> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(FollowEntity::getUid,uid);
        queryWrapper.eq(FollowEntity::getFollowUid,id);
        return baseMapper.selectCount(queryWrapper) != 0;
    }

    @Override
    public List<String> getFollowUid(String userId) {
        List<FollowEntity> list = this.lambdaQuery().eq(FollowEntity::getUid, userId).list();
        return list.stream().map(FollowEntity::getFollowUid).collect(Collectors.toList());
    }

    @Override
    public Integer isFollow(String uid, String followUid) {
        LambdaQueryWrapper<FollowEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FollowEntity::getUid, uid);
        lambdaQueryWrapper.eq(FollowEntity::getFollowUid, followUid);
        if(baseMapper.selectCount(lambdaQueryWrapper) == 0){
            return 0;
        }
        LambdaQueryWrapper<FollowEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FollowEntity::getUid, followUid);
        wrapper.eq(FollowEntity::getFollowUid, uid);
        return baseMapper.selectCount(wrapper) == 0 ? 2 : 1;
    }

    @Override
    public List<String> getFansList(String uid) {
        List<FollowEntity> list = this.lambdaQuery()
                .eq(FollowEntity::getFollowUid, uid)
                .orderByDesc(FollowEntity::getId)
                .list();
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        return list.stream().map(FollowEntity::getUid).collect(Collectors.toList());
    }

    @Override
    public List<String> getFollowUids(String uid) {
        List<FollowEntity> list = this.lambdaQuery()
                .eq(FollowEntity::getUid, uid)
                .list();
        return list.stream().map(FollowEntity::getFollowUid).collect(Collectors.toList());
    }


}
