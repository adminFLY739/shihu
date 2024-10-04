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

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.exception.LinfengException;
import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.entity.CommentThumbsEntity;
import cn.lili.modules.BBS.mapper.CommentThumbsDao;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.service.CommentThumbsService;
import cn.lili.modules.BBS.utils.DateUtil;
import cn.lili.modules.BBS.utils.PageUtils;
import cn.lili.modules.BBS.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service("commentThumbsService")
public class CommentThumbsServiceImpl extends ServiceImpl<CommentThumbsDao, CommentThumbsEntity> implements CommentThumbsService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentThumbsEntity> page = this.page(
                new Query<CommentThumbsEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 是否点赞
     * @param uid
     * @param id
     * @return
     */
    @Override
    public Boolean isThumbs(String uid, Long id) {
        CommentThumbsEntity one = baseMapper.selectOne(new LambdaQueryWrapper<CommentThumbsEntity>()
                .eq(CommentThumbsEntity::getCId, id)
                .eq(CommentThumbsEntity::getUid, uid));

        return Optional.ofNullable(one).isPresent();
    }

    @Override
    public Integer getThumbsCount(Long id) {
        return this.lambdaQuery()
                .eq(CommentThumbsEntity::getCId, id)
                .count().intValue();
    }

    /**
     * 点赞
     * @param request
     * @param uid
     */
    @Override
    public void addThumbs(AddThumbsForm request, String uid) {
        CommentThumbsEntity one=baseMapper.selectOne(new LambdaQueryWrapper<CommentThumbsEntity>()
        .eq(CommentThumbsEntity::getCId,request.getId())
        .eq(CommentThumbsEntity::getUid,uid));
        if(ObjectUtil.isNotNull(one)){
            throw new LinfengException("请勿重复点赞");
        }
        CommentThumbsEntity ct=new CommentThumbsEntity();
        ct.setUid(uid);
        ct.setCId(request.getId());
        ct.setCreateTime(DateUtil.nowDateTime());
        boolean save = this.save(ct);
        if(!save){
            throw new LinfengException("点赞失败");
        }
    }

    /**
     * 取消点赞
     * @param request
     * @param uid
     */
    @Override
    public void cancelThumbs(AddThumbsForm request, String uid) {
        baseMapper.delete(new LambdaQueryWrapper<CommentThumbsEntity>()
                .eq(CommentThumbsEntity::getCId,request.getId())
                .eq(CommentThumbsEntity::getUid,uid));
    }

    @Override
    public void cancelAllThumbs(List<CommentEntity> cList) {
        for(CommentEntity comment : cList){
            baseMapper.delete(new LambdaQueryWrapper<CommentThumbsEntity>()
                    .eq(CommentThumbsEntity::getCId,comment.getId()));
        }
    }


}
