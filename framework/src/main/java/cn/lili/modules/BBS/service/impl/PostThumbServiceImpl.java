package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.event.TransactionCommitSendMQEvent;
import cn.lili.common.exception.LinfengException;
import cn.lili.modules.BBS.entity.*;
import cn.lili.modules.BBS.mapper.PostThumbDao;
import cn.lili.modules.BBS.mapper.TaskUserDao;
import cn.lili.modules.BBS.param.AddPostThumbForm;
import cn.lili.modules.BBS.param.ManagerAddPostThumbForm;
import cn.lili.modules.BBS.service.EndorseMessageService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.service.PostThumbService;
import cn.lili.modules.BBS.service.TaskUserService;
import cn.lili.modules.BBS.utils.DateUtil;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dto.MemberPointMessage;
import cn.lili.modules.member.entity.enums.PointTypeEnum;
import cn.lili.modules.member.service.MemberService;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author wuwenxin
 * @date 2023-11-10 14:48:56
 **/
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbDao, PostThumbEntity> implements PostThumbService {
    @Resource
    private PostService postService;
    @Resource
    private EndorseMessageService endorseMessageService;
    @Resource
    private TaskUserService taskUserService;
    @Resource
    private MemberService memberService;

    @Override
    public Integer thumbCount(Integer postId) {
        return this.lambdaQuery()
                .eq(PostThumbEntity::getPostId,postId)
                .count().intValue();
    }

    @Override
    public Boolean isPostThumb(String uid, Integer postId) {
        PostThumbEntity entity = baseMapper.selectOne(
                new LambdaQueryWrapper<PostThumbEntity>()
                        .eq(PostThumbEntity::getPostId, postId)
                        .eq(PostThumbEntity::getUid, uid));
        return Optional.ofNullable(entity).isPresent();
    }

    @Override
    public void cancelPostThumb(AddPostThumbForm request, String uid) {
        baseMapper.delete(new LambdaQueryWrapper<PostThumbEntity>()
                .eq(PostThumbEntity::getPostId,request.getId())
                .eq(PostThumbEntity::getUid,uid));

        // 点赞数-1
        PostEntity post = postService.getById(request.getId());
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }
        post.setThumbCount(post.getThumbCount()-1);
        postService.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPostThumb(AddPostThumbForm request, String uid) {
        PostThumbEntity postThumbEntity = new PostThumbEntity();
        postThumbEntity.setUid(uid);
        postThumbEntity.setPostId(request.getId());
        baseMapper.insert(postThumbEntity);

        // 点赞数+1
        PostEntity post = postService.getById(request.getId());
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }
        post.setThumbCount(post.getThumbCount()+1);
        postService.updateById(post);

        // 消息通知
        EndorseMessageEntity endorseMessageEntity = new EndorseMessageEntity();
        endorseMessageEntity.setPostId(request.getId());
        endorseMessageEntity.setUid(uid);
        endorseMessageEntity.setIsRead(Boolean.FALSE);
        endorseMessageEntity.setCreateTime(DateUtil.nowDateTime());
        endorseMessageEntity.setReceiverUid(request.getUid());
        endorseMessageService.save(endorseMessageEntity);

        // 每日任务监测
        taskUserService.addTaskUser(uid,"3",3L,"赞同 1 篇认可的内容");
    }

    @Override
    public void ManagerAddPostThumb(ManagerAddPostThumbForm request) {
        PostThumbEntity postThumbEntity = new PostThumbEntity();
        postThumbEntity.setUid(request.getThumbUid());
        postThumbEntity.setPostId(request.getId());
        baseMapper.insert(postThumbEntity);

        // 点赞数+1
        PostEntity post = postService.getById(request.getId());
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }
        post.setThumbCount(post.getThumbCount()+1);
        postService.updateById(post);

        // 消息通知
        EndorseMessageEntity endorseMessageEntity = new EndorseMessageEntity();
        endorseMessageEntity.setPostId(request.getId());
        endorseMessageEntity.setUid(request.getThumbUid());
        endorseMessageEntity.setIsRead(Boolean.FALSE);
        endorseMessageEntity.setCreateTime(DateUtil.nowDateTime());
        endorseMessageEntity.setReceiverUid(request.getUid());
        endorseMessageService.save(endorseMessageEntity);
    }
}
