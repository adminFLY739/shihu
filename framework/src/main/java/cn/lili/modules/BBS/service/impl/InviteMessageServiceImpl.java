package cn.lili.modules.BBS.service.impl;



import cn.hutool.core.util.ObjectUtil;
import cn.lili.modules.BBS.entity.DiscussEntity;
import cn.lili.modules.BBS.entity.InviteMessageEntity;
import cn.lili.modules.BBS.entity.vo.DiscussListResponse;
import cn.lili.modules.BBS.entity.vo.InviteMessageResponse;
import cn.lili.modules.BBS.mapper.InviteMessageDao;

import cn.lili.modules.BBS.param.AddInviteForm;
import cn.lili.modules.BBS.service.*;
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
 * @date 2023-11-13 16:43:58
 **/
@Service
public class InviteMessageServiceImpl extends ServiceImpl<InviteMessageDao, InviteMessageEntity> implements InviteMessageService {
    @Resource
    private InviteMessageDao inviteMessageDao;
    @Resource
    private PostService postService;
    @Resource
    private MemberService memberService;
    @Resource
    private DiscussService discussService;
    @Resource
    private FollowDiscussService followDiscussService;

    @Override
    public AppPageUtils getInviteMessages(Integer currPage, String uid) {
        Page<InviteMessageEntity> page = new Page<>(currPage, 10);
        QueryWrapper<InviteMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("receiver_uid",uid);
        Page<InviteMessageEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<InviteMessageEntity> data = (List<InviteMessageEntity>) appPage.getData();

        // 创建响应对象列表
        ArrayList<InviteMessageResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            InviteMessageResponse inviteMessageResponse = new InviteMessageResponse();

            // 获取邀请回答的话题
            DiscussEntity discussEntity = discussService.getById(item.getDiscussId());
            if (ObjectUtil.isNull(discussEntity)){
                inviteMessageDao.deleteById(item.getId());
                return;
            }

            DiscussListResponse discussListResponse = new DiscussListResponse();
            BeanUtils.copyProperties(discussEntity, discussListResponse);

            // 关注量
            discussListResponse.setFollowCount(followDiscussService.getFollowCountByDiscussId(item.getId().intValue()));
            // 回答量
            discussListResponse.setPostCount(postService.getPostCountByDiscussId(item.getId().intValue()));

            // 获取邀请人信息
            Member userInfo = memberService.getById(item.getUid());
            if (ObjectUtil.isNull(userInfo)){
                return;
            }

            inviteMessageResponse.setLevel(getUserLevel(userInfo.getTotalPoint()));
            inviteMessageResponse.setId(item.getId());
            inviteMessageResponse.setUserInfo(userInfo);
            inviteMessageResponse.setDiscuss(discussListResponse);
            inviteMessageResponse.setIsRead(item.getIsRead());
            inviteMessageResponse.setCreateTime(item.getCreateTime());

            responseList.add(inviteMessageResponse);
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
            UpdateWrapper<InviteMessageEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageIds);

            // 使用LambdaUpdateWrapper精确指定要更新的字段
            LambdaUpdateWrapper<InviteMessageEntity> lambdaUpdateWrapper = updateWrapper.lambda();
            lambdaUpdateWrapper.set(InviteMessageEntity::getIsRead, 1);

            inviteMessageDao.update(null, lambdaUpdateWrapper);
        }
    }

    @Override
    public void addInviteMessage(AddInviteForm addInviteForm, String uid) {
        InviteMessageEntity inviteMessageEntity = new InviteMessageEntity();
        inviteMessageEntity.setUid(uid);
        inviteMessageEntity.setReceiverUid(addInviteForm.getUid());
        inviteMessageEntity.setIsRead(Boolean.FALSE);
        inviteMessageEntity.setDiscussId(addInviteForm.getDiscussId());
        inviteMessageEntity.setCreateTime(DateUtil.nowDateTime());
        inviteMessageDao.insert(inviteMessageEntity);
    }
}
