package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.modules.BBS.entity.*;
import cn.lili.modules.BBS.entity.vo.CommentMessageResponse;
import cn.lili.modules.BBS.entity.vo.MessageCountResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.*;
import cn.lili.modules.BBS.service.CommentMessageService;
import cn.lili.modules.BBS.service.CommentService;
import cn.lili.modules.BBS.service.PostCollectionService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-07 19:35:20
 **/
@Service
public class CommentMessageServiceImpl extends ServiceImpl<CommentMessageDao, CommentMessageEntity> implements CommentMessageService {

    @Resource
    private CommentMessageDao commentMessageDao;
    @Resource
    private EndorseMessageDao endorseMessageDao;
    @Resource
    private CollectMessageDao collectMessageDao;
    @Resource
    private FollowMessageDao followMessageDao;
    @Resource
    private InviteMessageDao inviteMessageDao;
    @Resource
    private CommentService commentService;
    @Resource
    private PostService postService;
    @Resource
    private PostCollectionService postCollectionService;

    @Resource
    private MemberService memberService;

    @Override
    public AppPageUtils getCommentMessages(Integer currPage,String uid) {
        Page<CommentMessageEntity> page = new Page<>(currPage, 10);
        QueryWrapper<CommentMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("receiver_uid",uid);
        Page<CommentMessageEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<CommentMessageEntity> data = (List<CommentMessageEntity>) appPage.getData();

        // 创建响应对象列表
        ArrayList<CommentMessageResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            CommentMessageResponse commentMessageResponse = new CommentMessageResponse();

            // 获取评论
            CommentEntity commentEntity = commentService.getById(item.getCommentId());
            if (ObjectUtil.isNull(commentEntity)){
                commentMessageDao.deleteById(item.getId());
                return;
            }

            // 获取评论的帖子
            PostEntity postEntity = postService.getById(commentEntity.getPostId());
            if (ObjectUtil.isNull(postEntity)){
                commentMessageDao.deleteById(item.getId());
                return;
            }


            PostListResponse postListResponse = new PostListResponse();
            BeanUtils.copyProperties(postEntity, postListResponse);
            // 设置帖子收藏数
            postListResponse.setCollectionCount(postCollectionService.collectCount(postListResponse.getId()));
            // 设置帖子评论数
            postListResponse.setCommentCount(commentService.getCountByTopicId(postListResponse.getId()));

            // 获取评论人信息
            Member userInfo = memberService.getById(commentEntity.getUid());
            if (ObjectUtil.isNull(userInfo)){
                return;
            }

            commentMessageResponse.setLevel(getUserLevel(userInfo.getTotalPoint()));
            commentMessageResponse.setId(item.getId());
            commentMessageResponse.setUserInfo(userInfo);
            commentMessageResponse.setPost(postListResponse);
            commentMessageResponse.setCreateTime(commentEntity.getCreateTime());
            commentMessageResponse.setContent(commentEntity.getContent());
            commentMessageResponse.setIsRead(item.getIsRead());

            responseList.add(commentMessageResponse);
        });

        appPage.setData(responseList);

        return appPage;
    }

    public Integer getUserLevel(Long totalPoint){
        int point = totalPoint.intValue();
        int level = 1;
        if (point >= 250 && point < 600) {
            level = 2;
        } else if (point >= 600 && point < 2500) {
            level = 3;
        } else if (point >= 2500 && point < 9500) {
            level = 4;
        } else if (point >= 9500) {
            level = 5;
        }
        return level;
    }

    @Override
    public void markMessagesAsRead(List<Long> messageIds) {
        if (messageIds != null && !messageIds.isEmpty()) {
            UpdateWrapper<CommentMessageEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageIds);

            // 使用LambdaUpdateWrapper精确指定要更新的字段
            LambdaUpdateWrapper<CommentMessageEntity> lambdaUpdateWrapper = updateWrapper.lambda();
            lambdaUpdateWrapper.set(CommentMessageEntity::getIsRead, 1);

            commentMessageDao.update(null, lambdaUpdateWrapper);
        }
    }

    @Override
    public MessageCountResponse getMessageNoReadCount(String uid) {
        MessageCountResponse messageCountResponse = new MessageCountResponse();

        messageCountResponse.setCommentMessageNoReadCount(getUnreadMessageCount(commentMessageDao, uid));
        messageCountResponse.setEndorseMessageNoReadCount(getUnreadMessageCount(endorseMessageDao,  uid));
        messageCountResponse.setCollectMessageNoReadCount(getUnreadMessageCount(collectMessageDao,  uid));
        messageCountResponse.setFollowMessageNoReadCount(getUnreadMessageCount(followMessageDao,  uid));
        messageCountResponse.setInviteMessageNoReadCount(getUnreadMessageCount(inviteMessageDao,  uid));

        return messageCountResponse;
    }

    private <T> Long getUnreadMessageCount(BaseMapper<T> messageDao, String uid) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_uid", uid);
        queryWrapper.eq("is_read", 0);
        return messageDao.selectCount(queryWrapper);
    }



    @Override
    public Integer getAllMessageNoReadCount(String uid) {
        MessageCountResponse messageNoReadCount = getMessageNoReadCount(uid);
        Integer commentMessageNoReadCount = messageNoReadCount.getCommentMessageNoReadCount().intValue();
        Integer collectMessageNoReadCount = messageNoReadCount.getCollectMessageNoReadCount().intValue();
        Integer endorseMessageNoReadCount = messageNoReadCount.getEndorseMessageNoReadCount().intValue();
        Integer followMessageNoReadCount = messageNoReadCount.getFollowMessageNoReadCount().intValue();
        Integer inviteMessageNoReadCount = messageNoReadCount.getInviteMessageNoReadCount().intValue();
        return commentMessageNoReadCount+collectMessageNoReadCount+endorseMessageNoReadCount+followMessageNoReadCount+inviteMessageNoReadCount;
    }


}
