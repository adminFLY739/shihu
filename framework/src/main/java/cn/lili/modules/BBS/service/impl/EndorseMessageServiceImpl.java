package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.exception.LinfengException;
import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.entity.CommentMessageEntity;
import cn.lili.modules.BBS.entity.EndorseMessageEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.entity.vo.CommentMessageResponse;
import cn.lili.modules.BBS.entity.vo.EndorseMessageResponse;
import cn.lili.modules.BBS.entity.vo.MessageCountResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.EndorseMessageDao;
import cn.lili.modules.BBS.service.CommentService;
import cn.lili.modules.BBS.service.EndorseMessageService;
import cn.lili.modules.BBS.service.PostCollectionService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.DateUtil;
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
 * @date 2023-11-09 22:34:15
 **/
@Service
public class EndorseMessageServiceImpl extends ServiceImpl<EndorseMessageDao, EndorseMessageEntity> implements EndorseMessageService {

    @Resource
    private EndorseMessageDao endorseMessageDao;

    @Resource
    private CommentService commentService;

    @Resource
    private PostService postService;

    @Resource
    private PostCollectionService postCollectionService;

    @Resource
    private MemberService memberService;

    @Override
    public AppPageUtils getEndorseMessages(Integer currPage, String uid) {
        Page<EndorseMessageEntity> page = new Page<>(currPage, 10);
        QueryWrapper<EndorseMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("receiver_uid",uid);
        Page<EndorseMessageEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<EndorseMessageEntity> data = (List<EndorseMessageEntity>) appPage.getData();

        // 创建响应对象列表
        ArrayList<EndorseMessageResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            EndorseMessageResponse commentMessageResponse = new EndorseMessageResponse();

            // 获取喜欢的帖子
            PostEntity postEntity = postService.getById(item.getPostId());

            // 帖子被删除时，不再提醒消息
            if (ObjectUtil.isNull(postEntity)) {
                endorseMessageDao.deleteById(item.getId());
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

            commentMessageResponse.setLevel(getUserLevel(userInfo.getTotalPoint()));
            commentMessageResponse.setId(item.getId());
            commentMessageResponse.setUserInfo(userInfo);
            commentMessageResponse.setPost(postListResponse);
            commentMessageResponse.setIsRead(item.getIsRead());
            commentMessageResponse.setCreateTime(item.getCreateTime());

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
            UpdateWrapper<EndorseMessageEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageIds);

            // 使用LambdaUpdateWrapper精确指定要更新的字段
            LambdaUpdateWrapper<EndorseMessageEntity> lambdaUpdateWrapper = updateWrapper.lambda();
            lambdaUpdateWrapper.set(EndorseMessageEntity::getIsRead, 1);

            endorseMessageDao.update(null, lambdaUpdateWrapper);
        }
    }
}
