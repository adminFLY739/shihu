package cn.lili.modules.BBS.service.impl;

import cn.lili.modules.BBS.entity.FollowDiscussEntity;
import cn.lili.modules.BBS.entity.FollowEntity;
import cn.lili.modules.BBS.mapper.FollowDiscussDao;
import cn.lili.modules.BBS.service.FollowDiscussService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wuwenxin
 * @date 2023-10-25 22:16:09
 **/
@Service
public class FollowDiscussServiceImpl extends ServiceImpl<FollowDiscussDao, FollowDiscussEntity> implements FollowDiscussService {
    @Override
    public Boolean isFollowOrNot(String uid, Integer discussId) {
        LambdaQueryWrapper<FollowDiscussEntity> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(FollowDiscussEntity::getUid,uid);
        queryWrapper.eq(FollowDiscussEntity::getDiscussId,discussId);
        return baseMapper.selectCount(queryWrapper) != 0;
    }

    @Override
    public List<Integer> getFollowDiscussIdsByUid(String uid) {
        return this.lambdaQuery()
                .eq(FollowDiscussEntity::getUid, uid)
                .list()
                .stream()
                .map(FollowDiscussEntity::getDiscussId)
                .collect(Collectors.toList());

    }

    @Override
    public Integer getFollowCountByDiscussId(Integer discussId) {
        return this.lambdaQuery()
                .eq(FollowDiscussEntity::getDiscussId, discussId)
                .count().intValue();
    }


}
