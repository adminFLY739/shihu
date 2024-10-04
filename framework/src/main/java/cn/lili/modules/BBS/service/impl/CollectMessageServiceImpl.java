package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.modules.BBS.entity.CollectMessageEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.entity.vo.CollectMessageResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.CollectMessageDao;
import cn.lili.modules.BBS.service.CollectMessageService;
import cn.lili.modules.BBS.service.CommentService;
import cn.lili.modules.BBS.service.PostCollectionService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-10 23:10:11
 **/
@Service
public class CollectMessageServiceImpl extends ServiceImpl<CollectMessageDao, CollectMessageEntity> implements CollectMessageService {

    @Resource
    private CollectMessageDao collectMessageDao;

    @Resource
    private CommentService commentService;

    @Resource
    private PostService postService;

    @Resource
    private PostCollectionService postCollectionService;

    @Resource
    private MemberService memberService;
    @Override
    public AppPageUtils getCollectMessages(Integer currPage, String uid) {
        Page<CollectMessageEntity> page = new Page<>(currPage, 10);
        QueryWrapper<CollectMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("receiver_uid",uid);
        Page<CollectMessageEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<CollectMessageEntity> data = (List<CollectMessageEntity>) appPage.getData();

        // 创建响应对象列表
        ArrayList<CollectMessageResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            CollectMessageResponse collectMessageResponse = new CollectMessageResponse();

            // 获取喜欢的帖子
            PostEntity postEntity = postService.getById(item.getPostId());
            if (ObjectUtil.isNull(postEntity)){
                collectMessageDao.deleteById(item.getId());
                return;
            }

            PostListResponse postListResponse = new PostListResponse();
            BeanUtils.copyProperties(postEntity, postListResponse);
            // 设置帖子收藏数
            postListResponse.setCollectionCount(postCollectionService.collectCount(postListResponse.getId()));
            // 设置帖子评论数
            postListResponse.setCommentCount(commentService.getCountByTopicId(postListResponse.getId()));

            // 获取赞同者信息
            Member userInfo = memberService.getById(item.getUid());
            if (ObjectUtil.isNull(userInfo)){
                return;
            }

            collectMessageResponse.setLevel(getUserLevel(userInfo.getTotalPoint()));
            collectMessageResponse.setId(item.getId());
            collectMessageResponse.setUserInfo(userInfo);
            collectMessageResponse.setPost(postListResponse);
            collectMessageResponse.setIsRead(item.getIsRead());
            collectMessageResponse.setCreateTime(item.getCreateTime());

            responseList.add(collectMessageResponse);
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
            UpdateWrapper<CollectMessageEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageIds);

            // 使用LambdaUpdateWrapper精确指定要更新的字段
            LambdaUpdateWrapper<CollectMessageEntity> lambdaUpdateWrapper = updateWrapper.lambda();
            lambdaUpdateWrapper.set(CollectMessageEntity::getIsRead, 1);

            collectMessageDao.update(null, lambdaUpdateWrapper);
        }
    }
}
