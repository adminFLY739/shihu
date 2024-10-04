package cn.lili.modules.BBS.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.LinfengException;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.modules.BBS.entity.*;
import cn.lili.modules.BBS.entity.vo.DiscussDetailResponse;
import cn.lili.modules.BBS.entity.vo.DiscussListResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.DiscussDao;
import cn.lili.modules.BBS.mapper.FollowDiscussDao;
import cn.lili.modules.BBS.mapper.PostDao;
import cn.lili.modules.BBS.param.AddCommentDiscussForm;
import cn.lili.modules.BBS.param.AddDiscussForm;
import cn.lili.modules.BBS.param.AddFollowDiscussForm;
import cn.lili.modules.BBS.param.DelCommentDiscussForm;
import cn.lili.modules.BBS.service.*;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.DateUtil;
import cn.lili.modules.BBS.utils.JsonUtils;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023/10/16 23:10:59
 */
@Service
public class DiscussServiceImpl extends ServiceImpl<DiscussDao, DiscussEntity> implements DiscussService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private PostDao postDao;

    @Resource
    private PostService postService;

    @Resource
    private PostCollectionService postCollectionService;

    @Resource
    private CommentService commentService;

    @Resource
    private DiscussDao discussDao;

    @Resource
    private MemberService memberService;

    @Resource
    private CommentDiscussService commentDiscussService;

    @Resource
    private FollowDiscussService followDiscussService;

    @Resource
    private FollowDiscussDao followDiscussDao;

    @Override
    public AppPageUtils lastDiscuss(Integer currPage) {
        Page<DiscussEntity> page = new Page<>(currPage, 10);
        QueryWrapper<DiscussEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("discuss_top","id");
        Page<DiscussEntity> pages = baseMapper.selectPage(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);
        List<DiscussEntity> data = (List<DiscussEntity>)appPage.getData();
        List<DiscussListResponse> responseList = new ArrayList<>();
        data.forEach(item -> {
            List<Integer> cateIdList = JSON.parseArray(item.getCut(), Integer.class);
            DiscussListResponse response = new DiscussListResponse();
            BeanUtils.copyProperties(item,response);

            //设置响应数据
            Member userInfo = memberService.getById(item.getUid());
            response.setUserInfo(userInfo);
            response.setLevel(getUserLevel(userInfo.getTotalPoint()));
            response.setPostCount(postService.getPostCountByDiscussId(item.getId().intValue()));
            response.setFollowCount(followDiscussService.getFollowCountByDiscussId(item.getId().intValue()));

            // 解析分类数据并设置到响应对象中
            List<String> cateNameList = new ArrayList<>();
            for (Integer cateId : cateIdList) {
                CategoryEntity category = categoryService.getById(cateId);
                cateNameList.add(category.getCateName());
            }
            response.setCutName(cateNameList);
            // 将响应对象添加到列表中
            responseList.add(response);
        });

        // 更新appPage的数据为响应对象列表
        appPage.setData(responseList);

        // 返回更新后的appPage对象
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
    public DiscussDetailResponse detail(Integer discussId,String uid) {
        DiscussEntity discuss = this.getById(discussId);
        if (ObjectUtil.isNull(discuss)) {
            throw new LinfengException("该话题不存在或已删除");
        }

        AuthUser user = UserContext.getCurrentUser();
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }

        discuss.setReadCount(discuss.getReadCount()+1);
        discussDao.updateById(discuss);

        DiscussDetailResponse discussDetailResponse = new DiscussDetailResponse();
        BeanUtils.copyProperties(discuss, discussDetailResponse);

        // 设置话题评论数
        discussDetailResponse.setCommentCount(commentDiscussService.getCommentDiscussCountByDiscussId(discussId));
        // 设置话题回答数
        discussDetailResponse.setPostCount(postService.getPostCountByDiscussId(discussId));
        // 查询是否关注该问题
        discussDetailResponse.setIsFollowDiscuss(followDiscussService.isFollowOrNot(uid,discussId));
        // 查询话题关注量
        discussDetailResponse.setFollowCount(followDiscussService.getFollowCountByDiscussId(discussId));


        //获取问题的所有回答
        List<PostEntity> postList = postDao.getPostListByDiscussId(discussId);
        List<PostListResponse> postListResponses = new ArrayList<>();
        postList.forEach(postEntity -> {
            PostListResponse response = new PostListResponse();
            // 将帖子实体的属性复制到响应对象中
            BeanUtils.copyProperties(postEntity, response);
            // 设置收藏数
            response.setCollectionCount(postCollectionService.collectCount(response.getId()));

            //回答帖子评论量
            response.setCommentCount(commentService.getCountByPostId(response.getId()));

            // 设置用户信息
            Member userInfo = memberService.getById(response.getUid());
            response.setUserInfo(userInfo);
            // 设置用户等级
            response.setLevel(getUserLevel(userInfo.getTotalPoint()));

            // 解析媒体数据并设置到响应对象中
            response.setMedia(JsonUtils.JsonToList(postEntity.getMedia()));

            // 解析分类数据并设置到响应对象中
            // 获取帖子的分类列表
            List<Integer> cateIdList = JSON.parseArray(postEntity.getCut(), Integer.class);
            List<String> cateNameList = new ArrayList<>();
            for (Integer cateId : cateIdList) {
                CategoryEntity category = categoryService.getById(cateId);
                cateNameList.add(category.getCateName());
            }
            response.setCutName(cateNameList);

            // 将响应对象添加到列表中
            postListResponses.add(response);
        });
        discussDetailResponse.setPostDetails(postListResponses);

        //分类处理
        List<Integer> cateIdList = JSON.parseArray(discuss.getCut(), Integer.class);
        List<String> cateNameList = new ArrayList<>();
        for (Integer cateId : cateIdList) {
            CategoryEntity category = categoryService.getById(cateId);
            cateNameList.add(category.getCateName());
        }
        discussDetailResponse.setCut(cateIdList);
        discussDetailResponse.setCutName(cateNameList);

        return discussDetailResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDiscuss(AddDiscussForm addDiscussForm, String uid) {
        DiscussEntity discuss = new DiscussEntity();
        BeanUtils.copyProperties(addDiscussForm, discuss);
        discuss.setDiscussTop(1);
        discuss.setPostCount(0);
        discuss.setReadCount(0);
        discuss.setCut(JSON.toJSONString(addDiscussForm.getCut()));
        discuss.setUid(uid);
        discuss.setCreateTime(DateUtil.nowDateTime());
        if (this.save(discuss)){
            return discuss.getId();
        }
        return 0L;
    }

    @Override
    public Long updateDiscuss(AddDiscussForm updateDiscussForm, String uid) {
        DiscussEntity discuss = this.getById(uid);
        discuss.setCut(JSON.toJSONString(updateDiscussForm.getCut()));
        BeanUtils.copyProperties(updateDiscussForm, discuss);
        baseMapper.updateById(discuss);
        return discuss.getId();
    }

    @Override
    public void addCommentDiscuss(AddCommentDiscussForm addCommentDiscussForm, String uid) {
        CommentDiscussEntity commentDiscuss = new CommentDiscussEntity();
        BeanUtils.copyProperties(addCommentDiscussForm, commentDiscuss);
        commentDiscuss.setCreateTime(DateUtil.nowDateTime());
        commentDiscuss.setUid(uid);
        commentDiscuss.setStatus(1);
        commentDiscussService.save(commentDiscuss);
    }

    @Override
    public void addFollowDiscuss(AddFollowDiscussForm addFollowDiscussForm, String uid) {
        Boolean isFollow = followDiscussService.isFollowOrNot(uid, addFollowDiscussForm.getDiscussId());
        if (isFollow) {
            throw new LinfengException("您已经关注过了哦,请刷新页面试试");
        }
        FollowDiscussEntity followDiscuss = new FollowDiscussEntity();
        followDiscuss.setDiscussId(addFollowDiscussForm.getDiscussId());
        followDiscuss.setUid(uid);
        followDiscuss.setCreateTime(DateUtil.nowDateTime());
        followDiscussService.save(followDiscuss);
    }

    @Override
    public void cancelFollowDiscuss(AddFollowDiscussForm addFollowDiscussForm, String uid) {
        followDiscussDao.cancelFollowDiscuss(uid,addFollowDiscussForm.getDiscussId());
    }

    @Override
    public AppPageUtils myFollowDiscuss(Integer currPage, String uid) {
        // 获取用户关注的问题id列表
        List<Integer> discussIds = followDiscussService.getFollowDiscussIdsByUid(uid);

        // 如果问题id列表为空，则返回空结果
        if (discussIds.isEmpty()) {
            return new AppPageUtils(new Page<>());
        }

        // 创建查询条件
        Page<DiscussEntity> page = new Page<>(currPage, 10);
        QueryWrapper<DiscussEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("discuss_top","id");
        queryWrapper.in("id", discussIds);

        // 查询关注的问题
        Page<DiscussEntity> pages = baseMapper.selectPage(page, queryWrapper);

        AppPageUtils appPage = new AppPageUtils(pages);
        List<DiscussEntity> data = (List<DiscussEntity>)appPage.getData();

        // 创建响应对象
        List<DiscussListResponse> responseList = new ArrayList<>();

        data.forEach(item -> {
            List<Integer> cateIdList = JSON.parseArray(item.getCut(), Integer.class);
            DiscussListResponse response = new DiscussListResponse();
            BeanUtils.copyProperties(item,response);

            //设置响应数据
            Member userInfo = memberService.getById(item.getUid());
            response.setUserInfo(userInfo);
            response.setLevel(getUserLevel(userInfo.getTotalPoint()));
            response.setPostCount(postService.getPostCountByDiscussId(item.getId().intValue()));


            // 解析分类数据并设置到响应对象中
            List<String> cateNameList = new ArrayList<>();
            for (Integer cateId : cateIdList) {
                CategoryEntity category = categoryService.getById(cateId);
                cateNameList.add(category.getCateName());
            }
            response.setCutName(cateNameList);
            // 将响应对象添加到列表中
            responseList.add(response);
        });

        // 更新appPage的数据为响应对象列表
        appPage.setData(responseList);

        // 返回更新后的appPage对象
        return appPage;
    }

    @Override
    public void delCommentDiscuss(DelCommentDiscussForm request, String uid) {
        commentDiscussService.deleteCommentDiscussById(request.getId());
    }
}
