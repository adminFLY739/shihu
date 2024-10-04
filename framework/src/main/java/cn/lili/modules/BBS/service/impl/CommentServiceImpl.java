/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 商业版演示站点: https://www.linfeng.tech
 * 商业版购买联系技术客服
 * QQ:  3582996245
 * 可正常分享和学习源码，不得专卖或非法牟利！
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.entity.vo.AppChildrenCommentResponse;
import cn.lili.modules.BBS.entity.vo.AppCommentResponse;
import cn.lili.modules.BBS.entity.vo.CommentResponse;
import cn.lili.modules.BBS.mapper.CommentDao;
import cn.lili.modules.BBS.service.CommentService;
import cn.lili.modules.BBS.service.CommentThumbsService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.Constant;
import cn.lili.modules.BBS.utils.PageUtils;
import cn.lili.modules.BBS.utils.Query;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {


    @Autowired
    private CommentThumbsService commentThumbsService;

    @Autowired
    private MemberService memberService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        String content = (String) params.get("content");
        queryWrapper
                .like(!ObjectUtil.isEmpty(content), "content", content);
        queryWrapper.lambda().orderByDesc(CommentEntity::getId);
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                queryWrapper);
        List<CommentEntity> data = page.getRecords();
        List<CommentResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            CommentResponse commentResponse = new CommentResponse();
            BeanUtils.copyProperties(l, commentResponse);
            commentResponse.setUserInfo(memberService.getById(l.getUid()));
            responseList.add(commentResponse);
        });
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
//        QueryWrapper<CommentEntity> queryWrapper=new QueryWrapper<>();
//        queryWrapper.lambda().orderByDesc(CommentEntity::getId);
//        IPage<CommentEntity> page = this.page(
//                new Query<CommentEntity>().getPage(params),
//                queryWrapper
//        );

    }

    @Override
    public Integer getCountByTopicId(Integer id) {
        return baseMapper.selectCount(new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getStatus, cn.lili.modules.BBS.utils.Constant.COMMENT_NORMAL)
                .eq(CommentEntity::getPostId, id)).intValue();
    }

    @Override
    public void deleteByPid(Integer id) {
        LambdaQueryWrapper<CommentEntity> queryWrapper = new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getPostId, id);
        List<CommentEntity> comList = baseMapper.selectList(queryWrapper);
        baseMapper.delete(queryWrapper);
        commentThumbsService.cancelAllThumbs(comList);
    }

    @Override
    public void deleteById(Long id) {
        LambdaQueryWrapper<CommentEntity> queryWrapper = new LambdaQueryWrapper<CommentEntity>()
                .eq(CommentEntity::getId, id);
        List<CommentEntity> comList = baseMapper.selectList(queryWrapper);
        baseMapper.delete(queryWrapper);
        commentThumbsService.cancelAllThumbs(comList);
    }


//    /**
//     * 管理端删除评论
//     * @param id
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteByAdmin(Long id) {
//            this.deleteById(id);
//    }


    @Override
    public Integer getCountByPostId(Integer id) {
        return this.lambdaQuery()
                .eq(CommentEntity::getStatus, cn.lili.modules.BBS.utils.Constant.COMMENT_NORMAL)
                .eq(CommentEntity::getPostId, id).count().intValue();
    }


    @Override
    public AppPageUtils queryCommentPage(Integer postId, Integer page) {
        Page<CommentEntity> commentPage = new Page<>(page, Integer.MAX_VALUE);
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.lambda().eq(CommentEntity::getPostId, postId);
        queryWrapper.lambda().eq(CommentEntity::getStatus, Constant.COMMENT_NORMAL);
        Page<CommentEntity> pages = baseMapper.selectPage(commentPage, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);
        List<CommentEntity> data = (List<CommentEntity>) appPage.getData();

        Map<Integer, AppCommentResponse> responseMap = new HashMap<>();

        AuthUser authUser = UserContext.getCurrentUser();


        // 一级评论处理
        data.forEach(l -> {
            if (l.getPid() == 0) {
                AppCommentResponse response = new AppCommentResponse();
                BeanUtils.copyProperties(l, response);

                Member userInfo = memberService.getById(response.getUid());
                // 设置评论人信息
                response.setUserInfo(userInfo);
                // 评论的点赞数
                response.setThumbs(commentThumbsService.getThumbsCount(l.getId()));

                // 用户等级
                response.setLevel(getUserLevel(userInfo.getTotalPoint()));

                // 子评论初始化
                response.setChildren(new ArrayList<>());
                if (authUser == null) {
                    response.setIsThumbs(false);
                } else {
                    response.setIsThumbs(commentThumbsService.isThumbs(authUser.getId(), l.getId()));
                }
                responseMap.put(l.getId().intValue(), response);
            }
        });

        // 二级评论处理
        data.forEach(l -> {
            if (l.getPid() != 0) {
                AppCommentResponse response = responseMap.get(l.getPid());

                // 创建子评论
                AppChildrenCommentResponse childrenComment = new AppChildrenCommentResponse();
                BeanUtils.copyProperties(l, childrenComment);


                // 设置回复人信息
                Member userInfo = memberService.getById(childrenComment.getUid());
                childrenComment.setUserInfo(userInfo);
                // 回复人等级
                childrenComment.setLevel(getUserLevel(userInfo.getTotalPoint()));

                // 设置被回复人信息
                Member toUserInfo = memberService.getById(l.getToUid());
                childrenComment.setToUser(toUserInfo);
                // 被回复人等级
                childrenComment.setLevelToUser(toUserInfo == null ? null : getUserLevel(toUserInfo.getTotalPoint()));

                // 评论的点赞数
                childrenComment.setThumbs(commentThumbsService.getThumbsCount(l.getId()));

                if (authUser == null) {
                    childrenComment.setIsThumbs(false);
                } else {
                    childrenComment.setIsThumbs(commentThumbsService.isThumbs(authUser.getId(), l.getId()));
                }

                // 将子评论添加到父评论属性中
                response.getChildren().add(childrenComment);
            }
        });

        List<AppCommentResponse> responseList = new ArrayList<>(responseMap.values());

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

//    /**
//     * 获取昨天评论数
//     * @return
//     */
//    @Override
//    public Integer getYesterdayCount() {
//        DateTime yesterday = DateUtil.yesterday();
//        return this.lambdaQuery()
//                .ge(CommentEntity::getCreateTime,yesterday)
//                .eq(CommentEntity::getStatus,  Constant.COMMENT_NORMAL)
//                .count();
//    }
//
//    @Override
//    public Integer getAllCount() {
//        return this.lambdaQuery()
//                .eq(CommentEntity::getStatus, Constant.COMMENT_NORMAL)
//                .count();
//    }

}
