package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.exception.LinfengException;
import cn.lili.modules.BBS.entity.CommentDiscussEntity;
import cn.lili.modules.BBS.entity.CommentDiscussThumbsEntity;
import cn.lili.modules.BBS.mapper.CommentDiscussThumbsDao;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.service.CommentDiscussThumbsService;
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

/**
 * @author wuwenxin
 * @date 2023-10-21 20:06:25
 **/
@Service
public class CommentDiscussThumbsServiceImpl extends ServiceImpl<CommentDiscussThumbsDao, CommentDiscussThumbsEntity> implements CommentDiscussThumbsService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentDiscussThumbsEntity> page = this.page(
                new Query<CommentDiscussThumbsEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean isThumbs(String uid, Long id) {
        CommentDiscussThumbsEntity one = baseMapper.selectOne(new LambdaQueryWrapper<CommentDiscussThumbsEntity>()
                .eq(CommentDiscussThumbsEntity::getCommentDiscussId, id)
                .eq(CommentDiscussThumbsEntity::getUid, uid));

        return Optional.ofNullable(one).isPresent();
    }

    @Override
    public Integer getThumbsCount(Long id) {
        return this.lambdaQuery()
                .eq(CommentDiscussThumbsEntity::getCommentDiscussId, id)
                .count().intValue();
    }

    /**
     * 点赞
     * @param addThumbsForm
     * @param uid
     */
    @Override
    public void addThumbs(AddThumbsForm addThumbsForm, String uid) {
        CommentDiscussThumbsEntity one = baseMapper.selectOne(new LambdaQueryWrapper<CommentDiscussThumbsEntity>()
                .eq(CommentDiscussThumbsEntity::getCommentDiscussId, addThumbsForm.getId())
                .eq(CommentDiscussThumbsEntity::getUid, uid));
        if (ObjectUtil.isNotNull(one)) {
            throw new LinfengException("请勿重复点赞");
        }
        CommentDiscussThumbsEntity ct = new CommentDiscussThumbsEntity();
        ct.setUid(uid);
        ct.setCommentDiscussId(addThumbsForm.getId());
        ct.setCreateTime(DateUtil.nowDateTime());
        boolean save = this.save(ct);
        if (!save) {
            throw new LinfengException("点赞失败");
        }
    }

    /**
     * 取消点赞
     * @param addThumbsForm
     * @param uid
     */
    @Override
    public void cancelThumbs(AddThumbsForm addThumbsForm, String uid) {
        baseMapper.delete(new LambdaQueryWrapper<CommentDiscussThumbsEntity>()
                .eq(CommentDiscussThumbsEntity::getCommentDiscussId,addThumbsForm.getId())
                .eq(CommentDiscussThumbsEntity::getUid,uid));
    }

    @Override
    public void cancelAllThumbs(List<CommentDiscussEntity> commentDiscussList) {
        for(CommentDiscussEntity commentDiscuss : commentDiscussList){
            baseMapper.delete(new LambdaQueryWrapper<CommentDiscussThumbsEntity>()
                    .eq(CommentDiscussThumbsEntity::getCommentDiscussId,commentDiscuss.getId()));
        }
    }
}
