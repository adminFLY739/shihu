package cn.lili.modules.BBS.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.lili.modules.BBS.entity.EndorseMessageEntity;
import cn.lili.modules.BBS.entity.FollowMessageEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.BBS.entity.vo.EndorseMessageResponse;
import cn.lili.modules.BBS.entity.vo.FollowMessageResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.FollowMessageDao;
import cn.lili.modules.BBS.service.*;
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
 * @date 2023-11-11 16:04:53
 **/
@Service
public class FollowMessageServiceImpl extends ServiceImpl<FollowMessageDao, FollowMessageEntity> implements FollowMessageService {
    @Resource
    private FollowMessageDao followMessageDao;
    @Resource
    private FollowService followService;
    @Resource
    private MemberService memberService;

    @Override
    public AppPageUtils getFollowMessages(Integer currPage, String uid) {
        Page<FollowMessageEntity> page = new Page<>(currPage, 10);
        QueryWrapper<FollowMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("receiver_uid",uid);
        Page<FollowMessageEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<FollowMessageEntity> data = (List<FollowMessageEntity>) appPage.getData();

        // 创建响应对象列表
        ArrayList<FollowMessageResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            FollowMessageResponse followMessageResponse = new FollowMessageResponse();

            // 获取关注者信息
            Member userInfo = memberService.getById(item.getUid());
            if (ObjectUtil.isNull(userInfo)){
                return;
            }

            followMessageResponse.setLevel(getUserLevel(userInfo.getTotalPoint()));
            // 是否关注
            followMessageResponse.setIsFollow(followService.isFollowOrNot(uid, userInfo.getId()));

            followMessageResponse.setId(item.getId());
            followMessageResponse.setUserInfo(userInfo);
            followMessageResponse.setIsRead(item.getIsRead());
            followMessageResponse.setCreateTime(item.getCreateTime());

            responseList.add(followMessageResponse);
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
        UpdateWrapper<FollowMessageEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", messageIds);

        // 使用LambdaUpdateWrapper精确指定要更新的字段
        LambdaUpdateWrapper<FollowMessageEntity> lambdaUpdateWrapper = updateWrapper.lambda();
        lambdaUpdateWrapper.set(FollowMessageEntity::getIsRead, 1);

        followMessageDao.update(null, lambdaUpdateWrapper);
    }
}
