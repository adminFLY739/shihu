package cn.lili.modules.BBS.service.impl;

import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.modules.BBS.entity.CommentDiscussEntity;
import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.entity.vo.AppChildrenCommentDiscussResponse;
import cn.lili.modules.BBS.entity.vo.AppCommentDiscussResponse;
import cn.lili.modules.BBS.mapper.CommentDiscussDao;
import cn.lili.modules.BBS.service.CommentDiscussService;
import cn.lili.modules.BBS.service.CommentDiscussThumbsService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.Constant;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentDiscussServiceImpl extends ServiceImpl<CommentDiscussDao, CommentDiscussEntity> implements CommentDiscussService {

    @Resource
    private MemberService memberService;

    @Resource
    private CommentDiscussThumbsService commentDiscussThumbsService;

    @Override
    public Integer getCommentDiscussCountByDiscussId(Integer discussId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<CommentDiscussEntity>()
                .eq(CommentDiscussEntity::getStatus, cn.lili.modules.BBS.utils.Constant.COMMENT_NORMAL)
                .eq(CommentDiscussEntity::getDiscussId, discussId)).intValue();
    }

    @Override
    public AppPageUtils queryCommentPage(Integer discussId, Integer page) {
        Page<CommentDiscussEntity> commentDiscussPage = new Page<>(page,Integer.MAX_VALUE);
        QueryWrapper<CommentDiscussEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.lambda().eq(CommentDiscussEntity::getDiscussId, discussId);
        queryWrapper.lambda().eq(CommentDiscussEntity::getStatus, Constant.COMMENT_NORMAL);

        Page<CommentDiscussEntity> pages = baseMapper.selectPage(commentDiscussPage, queryWrapper);

        AppPageUtils appPage = new AppPageUtils(pages);
        List<CommentDiscussEntity> data = (List<CommentDiscussEntity>) appPage.getData();

        Map<Integer, AppCommentDiscussResponse> responseMap = new HashMap<>();

        AuthUser authUser = UserContext.getCurrentUser();

        // 一级评论处理
        data.forEach(item -> {
            if (item.getPid() == 0) {
                AppCommentDiscussResponse response = new AppCommentDiscussResponse();
                BeanUtils.copyProperties(item, response);

                // 设置评论人信息
                Member userInfo = memberService.getById(response.getUid());
                response.setUserInfo(userInfo);
                // 设置用户等级
                response.setLevel(getUserLevel(userInfo.getTotalPoint()));
                // 子评论初始化
                response.setChildren(new ArrayList<>());
                // 评论的点赞数
                response.setThumbs(commentDiscussThumbsService.getThumbsCount(item.getId()));

                if (authUser == null) {
                    response.setIsThumbs(false);
                } else {
                    response.setIsThumbs(commentDiscussThumbsService.isThumbs(authUser.getId(), item.getId()));
                }
                responseMap.put(item.getId().intValue(), response);
            }
        });

        // 二级评论处理
        data.forEach(l -> {
            if (l.getPid() != 0) {
                AppCommentDiscussResponse response = responseMap.get(l.getPid());

                // 创建子评论
                AppChildrenCommentDiscussResponse childrenComment = new AppChildrenCommentDiscussResponse();
                BeanUtils.copyProperties(l, childrenComment);

                // 设置回复人信息
                Member userInfo = memberService.getById(childrenComment.getUid());
                childrenComment.setUserInfo(userInfo);
                childrenComment.setLevel(getUserLevel(userInfo.getTotalPoint()));

                // 设置被回复人信息
                Member toUserInfo = memberService.getById(childrenComment.getToUid());
                childrenComment.setToUser(toUserInfo);
                childrenComment.setLevelToUser(toUserInfo == null ? null : getUserLevel(toUserInfo.getTotalPoint()));

                // 评论的点赞数
                childrenComment.setThumbs(commentDiscussThumbsService.getThumbsCount(l.getId()));

                if (authUser == null) {
                    childrenComment.setIsThumbs(false);
                } else {
                    childrenComment.setIsThumbs(commentDiscussThumbsService.isThumbs(authUser.getId(), l.getId()));
                }

                // 将子评论添加到父评论属性中
                response.getChildren().add(childrenComment);
            }
        });

        List<AppCommentDiscussResponse> responseList = new ArrayList<>(responseMap.values());

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
    public void deleteCommentDiscussById(Long id) {
        LambdaQueryWrapper<CommentDiscussEntity> queryWrapper = new LambdaQueryWrapper<CommentDiscussEntity>()
                .eq(CommentDiscussEntity::getId, id);
        List<CommentDiscussEntity> comList = baseMapper.selectList(queryWrapper);
        baseMapper.delete(queryWrapper);
        commentDiscussThumbsService.cancelAllThumbs(comList);
    }

}
