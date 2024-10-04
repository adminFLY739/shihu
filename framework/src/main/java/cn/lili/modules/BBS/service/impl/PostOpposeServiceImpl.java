package cn.lili.modules.BBS.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.exception.LinfengException;
import cn.lili.modules.BBS.entity.PostCollectionEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.entity.PostOpposeEntity;
import cn.lili.modules.BBS.entity.PostThumbEntity;
import cn.lili.modules.BBS.mapper.PostOpposeDao;
import cn.lili.modules.BBS.param.AddPostOpposeForm;
import cn.lili.modules.BBS.service.PostOpposeService;
import cn.lili.modules.BBS.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wuwenxin
 * @date 2023-11-10 14:45:49
 **/
@Service
public class PostOpposeServiceImpl  extends ServiceImpl<PostOpposeDao, PostOpposeEntity> implements PostOpposeService {

    @Resource
    private PostService postService;

    @Override
    public Integer opposeCount(Integer postId) {
        return this.lambdaQuery()
                .eq(PostOpposeEntity::getPostId,postId)
                .count().intValue();
    }

    @Override
    public Boolean isPostOppose(String uid, Integer postId) {
        PostOpposeEntity entity = baseMapper.selectOne(
                new LambdaQueryWrapper<PostOpposeEntity>()
                        .eq(PostOpposeEntity::getPostId, postId)
                        .eq(PostOpposeEntity::getUid, uid));
        return Optional.ofNullable(entity).isPresent();
    }

    @Override
    public void cancelPostOppose(AddPostOpposeForm request, String uid) {
        baseMapper.delete(new LambdaQueryWrapper<PostOpposeEntity>()
                .eq(PostOpposeEntity::getPostId,request.getId())
                .eq(PostOpposeEntity::getUid,uid));

        // 反对数-1
        PostEntity post = postService.getById(request.getId());
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }
        post.setOpposeCount(post.getOpposeCount()-1);
        postService.updateById(post);
    }

    @Override
    public void addPostOppose(AddPostOpposeForm request, String uid) {
        PostOpposeEntity postOpposeEntity = new PostOpposeEntity();
        postOpposeEntity.setUid(uid);
        postOpposeEntity.setPostId(request.getId());
        baseMapper.insert(postOpposeEntity);

        // 反对数+1
        PostEntity post = postService.getById(request.getId());
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }
        post.setOpposeCount(post.getOpposeCount()+1);
        postService.updateById(post);

    }
}
