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
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.LinfengException;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.modules.BBS.entity.*;
import cn.lili.modules.BBS.entity.vo.PostDetailResponse;
import cn.lili.modules.BBS.entity.vo.PostListResponse;
import cn.lili.modules.BBS.mapper.PostDao;
import cn.lili.modules.BBS.param.*;
import cn.lili.modules.BBS.service.*;
import cn.lili.modules.BBS.utils.*;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.robot.serviceImpl.RobotServiceImpl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service("postService")
public class PostServiceImpl extends ServiceImpl<PostDao, PostEntity> implements PostService {
    @Resource
    private PostThumbService postThumbService;
    @Resource
    private PostOpposeService postOpposeService;

    @Autowired
    private PostCollectionService postCollectionService;
    @Autowired
    private CommentService commentService;


    @Resource
    private CollectMessageService collectMessageService;

    @Autowired
    private FollowService followService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CategoryService categoryService;
    @Resource
    private TaskUserService taskUserService;

    @Resource
    private CommentMessageService commentMessageService;
    @Autowired
    private RobotServiceImpl robotServiceImpl;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String id = (String) params.get("id");
//        String status = (String)params.get("status");
        queryWrapper
                .like(!ObjectUtil.isEmpty(content), "content", content)
                .or()
                .like(!ObjectUtil.isEmpty(title), "title", title)
                .or().eq(!ObjectUtil.isEmpty(id), "id", id);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        IPage<PostEntity> page = this.page(
                new Query<PostEntity>().getPage(params),
                queryWrapper
        );
        List<PostEntity> data = page.getRecords();

        List<PostListResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            PostListResponse response = new PostListResponse();
            BeanUtils.copyProperties(l, response);
            response.setCollectionCount(postCollectionService.collectCount(response.getId()));
            response.setCommentCount(commentService.getCountByTopicId(response.getId()));
            response.setUserInfo(memberService.getById(response.getUid()));
            response.setMedia(JsonUtils.JsonToList(l.getMedia()));
            List<Integer> cateIdList = JSON.parseArray(l.getCut(), Integer.class);// 分类处理
            List<String> cateNameList = new ArrayList<>();
            for (Integer cateId : cateIdList) {
                CategoryEntity category = categoryService.getById(cateId);
                cateNameList.add(category.getCateName());
            }
            response.setCutName(cateNameList);
            responseList.add(response);
        });
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAdmin(Integer id) {

        baseMapper.delete(new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getId, id));
        commentService.deleteByPid(id);
        postCollectionService.cancelALLCollection(id);

    }

    @Override
    public Integer getPostNumByUid(String uid) {

        return this.lambdaQuery()
                .eq(PostEntity::getUid, uid)
                .count().intValue();
    }

    @Override
    public Long getPostNumByCut(Integer cut) {

        return this.lambdaQuery()
                .eq(PostEntity::getCut, cut)
                .count();
    }

    @Override
    public AppPageUtils lastPost(Integer currPage, Integer classId) {
        Page<PostEntity> page = new Page<>(currPage, 10);
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("post_top", "id");
        // discuss_id=0为帖子,不为0是问题的回答
        queryWrapper.eq("discuss_id", "0");
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            // 未登录将查询用户id为0的帖子
            return this.mapPostList(page, queryWrapper, "0");
        }
        return this.mapLastPostList(page, queryWrapper, authUser.getId(), classId);
    }

    @Override
    public AppPageUtils followUserPost(Integer page, String userId) {
        List<String> list = followService.getFollowUid(userId);
        if (list.isEmpty()) {
            return null;
        }
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(PostEntity::getUid, list);
        queryWrapper.orderByDesc("post_top", "id");
        Page<PostEntity> pages = new Page<>(page, 10);
        return this.mapPostList(pages, queryWrapper, userId);
    }

    @Override
    public void addCollection(AddCollectionForm request, String userId) {
        Boolean collection = postCollectionService.isCollection(userId, request.getId());
        if (collection) {
            throw new LinfengException("请勿重复点赞");
        }
        PostCollectionEntity pc = new PostCollectionEntity();
        pc.setPostId(request.getId());
        pc.setUid(userId);
        postCollectionService.save(pc);

        // 消息通知
        CollectMessageEntity collectMessageEntity = new CollectMessageEntity();
        collectMessageEntity.setPostId(request.getId());
        collectMessageEntity.setUid(userId);
        collectMessageEntity.setIsRead(Boolean.FALSE);
        collectMessageEntity.setCreateTime(DateUtil.nowDateTime());
        collectMessageEntity.setReceiverUid(request.getUid());
        collectMessageService.save(collectMessageEntity);
    }

    @Override
    public void ManagerAddPostCollection(ManagerAddCollectionForm request) {
        PostCollectionEntity pc = new PostCollectionEntity();
        pc.setPostId(request.getId());
        pc.setUid(request.getThumbUid());
        postCollectionService.save(pc);

        // 消息通知
        CollectMessageEntity collectMessageEntity = new CollectMessageEntity();
        collectMessageEntity.setPostId(request.getId());
        collectMessageEntity.setUid(request.getThumbUid());
        collectMessageEntity.setIsRead(Boolean.FALSE);
        collectMessageEntity.setCreateTime(DateUtil.nowDateTime());
        collectMessageEntity.setReceiverUid(request.getUid());
        collectMessageService.save(collectMessageEntity);
    }

    @Override
    public AppPageUtils myPost(Integer page, String uid) {
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getUid, uid);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        Page<PostEntity> pages = new Page<>(page, 10);
        return this.mapPostList(pages, queryWrapper, uid);
    }

    @Override
    public AppPageUtils myCollectPost(Integer page, String uid) {
        List<Integer> postIdList = postCollectionService.getPostListByUid(uid);
        if (postIdList.size() == 0) {
            return new AppPageUtils(null, 0, 10, 1);
        }
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(PostEntity::getId, postIdList);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        Page<PostEntity> pages = new Page<>(page, 10);
        return this.mapPostList(pages, queryWrapper, uid);
    }

    @Override
    public PostDetailResponse detail(Integer id) {
        PostEntity post = this.getById(id);
        if (ObjectUtil.isNull(post)) {
            throw new LinfengException("该帖子不存在或已删除");
        }

        AuthUser user = UserContext.getCurrentUser();
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        // AppUserEntity user = localUser.getUser();

        // 阅读量+1
        post.setReadCount(post.getReadCount() + 1);
        // 更新阅读量
        baseMapper.updateById(post);

        // 创建帖子响应对象
        PostDetailResponse response = new PostDetailResponse();
        BeanUtils.copyProperties(post, response);

        // AppUserEntity userInfo = appUserService.getById(post.getUid());
        Member userInfo = memberService.getById(post.getUid());
        if (userInfo == null) {
            userInfo = new Member();
            Robot robotInfo = robotServiceImpl.getById(response.getUid());
            userInfo.setUsername(robotInfo.getUsername());
            userInfo.setStudentId(robotInfo.getStudentId());
            userInfo.setNickName(robotInfo.getNickName());
            userInfo.setSex(robotInfo.getSex());
            userInfo.setBirthday(robotInfo.getBirthday());
            userInfo.setRegionId(robotInfo.getRegionId());
            userInfo.setRegion(robotInfo.getRegion());
            userInfo.setMobile(robotInfo.getMobile());
            userInfo.setPoint(robotInfo.getPoint());
            userInfo.setTotalPoint(robotInfo.getTotalPoint());
            userInfo.setFace(robotInfo.getFace());
            userInfo.setDisabled(robotInfo.getDisabled());
            userInfo.setHaveStore(robotInfo.getHaveStore());
            userInfo.setStoreId(robotInfo.getStoreId());
            userInfo.setClientEnum(robotInfo.getClientEnum());
            userInfo.setLastLoginDate(robotInfo.getLastLoginDate());
            userInfo.setGradeId(robotInfo.getGradeId());
            userInfo.setExperience(robotInfo.getExperience());
            userInfo.setTenantIds(robotInfo.getTenantIds());
        }
        // 封装用户信息
        response.setUserInfo(userInfo);

        // 用户等级
        response.setLevel(getUserLevel(userInfo.getTotalPoint()));

        if (ObjectUtil.isNull(user)) {
            response.setIsFollow(false);
            response.setIsCollection(false);
        } else {
            // 查询是否关注
            response.setIsFollow(followService.isFollowOrNot(user.getId(), post.getUid()));
            // 查询是否收藏
            response.setIsCollection(postCollectionService.isCollection(user.getId(), post.getId()));
            // 查询是否赞同
            response.setIsPostThumb(postThumbService.isPostThumb(user.getId(), post.getId()));
            // 查询是否反对
            response.setIsPostOppose(postOpposeService.isPostOppose(user.getId(), post.getId()));
        }
        response.setCollectionCount(postCollectionService.collectCount(post.getId()));
        // 评论量
        response.setCommentCount(commentService.getCountByPostId(post.getId()));
        response.setMedia(JsonUtils.JsonToList(post.getMedia()));// 文件处理
        List<Integer> cateIdList = JSON.parseArray(post.getCut(), Integer.class);// 分类处理
        List<String> cateNameList = new ArrayList<>();
        for (Integer cateId : cateIdList) {
            CategoryEntity category = categoryService.getById(cateId);
            cateNameList.add(category.getCateName());
        }
        response.setCut(cateIdList);
        response.setCutName(cateNameList);

        return response;
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
    public void addComment(AddCommentForm request, String uid) {
        // Member member = memberService.getById(uid);

        // if (!member.getDisabled()) {
        //     throw new LinfengException("您的账号未实名，请实名认证后再发布！");
        // }

        CommentEntity commentEntity = new CommentEntity();
        BeanUtils.copyProperties(request, commentEntity);

        commentEntity.setCreateTime(DateUtil.nowDateTime());
        commentEntity.setUid(uid);

        commentService.save(commentEntity);
        System.out.println("commentEntitycommentEntitycommentEntity" + commentEntity);

        // 创建评论消息(后续判断评论的是否为自己的帖子，如果是则不创建消息)
        CommentMessageEntity commentMessageEntity = new CommentMessageEntity();
        commentMessageEntity.setIsRead(Boolean.FALSE);
        if (request.getReceiverUid() != null) {
            commentMessageEntity.setReceiverUid(request.getReceiverUid());
            commentMessageEntity.setCommentId(commentEntity.getId());
            commentMessageService.save(commentMessageEntity);
        }
    }
    @Override
    public void addManagerComment(AddManagerCommentForm request) {
        CommentEntity commentEntity = new CommentEntity();
        BeanUtils.copyProperties(request, commentEntity);

        commentEntity.setCreateTime(DateUtil.nowDateTime());

        commentService.save(commentEntity);
        System.out.println("commentEntity-commentEntity" + commentEntity);

        // 创建评论消息(后续判断评论的是否为自己的帖子，如果是则不创建消息)
        CommentMessageEntity commentMessageEntity = new CommentMessageEntity();
        commentMessageEntity.setIsRead(Boolean.FALSE);
        if (request.getReceiverUid() != null) {
            commentMessageEntity.setReceiverUid(request.getReceiverUid());
            commentMessageEntity.setCommentId(commentEntity.getId());
            commentMessageService.save(commentMessageEntity);
        }



    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPost(AddPostForm request, String uid) {
        // Member member = memberService.getById(uid);
        //
        // if (!member.getDisabled()) {
        //     throw new LinfengException("您的账号未实名，请实名认证后再发布！");
        // }
        PostEntity post = new PostEntity();
        BeanUtils.copyProperties(request, post);
        post.setMedia(JSON.toJSONString(request.getMedia()));
        post.setCut(JSON.toJSONString(request.getCut()));
        post.setUid(uid);
        post.setCreateTime(DateUtil.nowDateTime());
        if (this.save(post)) {
            // 每日任务监测
            if (post.getContent().length() >= 20 && request.getMedia().size()>=3){
                taskUserService.addTaskUser(uid,"2",15L,"发布 1 条 20 字以上带 3 图的帖子");
            }
            if (post.getContent().length() >= 100){
                taskUserService.addTaskUser(uid,"1",5L,"发布 1 篇 100 字以上的回答(或帖子)");
            }
            return post.getId();
        }
        return 0;
    }

    @Override
    public Integer addManagerPost(AddManagerPostForm request) {
        PostEntity post = new PostEntity();
        BeanUtils.copyProperties(request, post);
        post.setMedia(JSON.toJSONString(request.getMedia()));
        post.setCut(JSON.toJSONString(request.getCut()));
        post.setCreateTime(DateUtil.nowDateTime());
        this.save(post);
        return post.getId();
    }

    @Override
    public void delComment(DelCommentForm request, String uid) {
        commentService.deleteById(request.getId());
    }

    @Override
    public void delPost(AddCollectionForm request, String uid) {
        baseMapper.delete(new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getId, request.getId())
                .eq(PostEntity::getUid, uid));
        commentService.deleteByPid(request.getId());
        postCollectionService.cancelALLCollection(request.getId());
    }

    @Override
    public Integer updatePost(AddPostForm request, String uid) {
        PostEntity post = this.getById(request.getId());
        post.setMedia(JSON.toJSONString(request.getMedia()));
        post.setCut(JSON.toJSONString(request.getCut()));
        BeanUtils.copyProperties(request, post);
        baseMapper.updateById(post);
        return post.getId();
    }

    @Override
    public Integer getPostCountByDiscussId(Integer id) {
        return baseMapper.selectCount(new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getStatus, cn.lili.modules.BBS.utils.Constant.COMMENT_DOWN)
                .eq(PostEntity::getDiscussId, id)).intValue();
    }

    @Override
    public AppPageUtils queryPageList(PostListForm request, String uid) {
        // 定义返回结果对象
        AppPageUtils appPage;

        // 创建分页对象，设置当前页和每页显示数量
        Page<PostEntity> page = new Page<>(request.getPage(), 10);

        // 创建查询条件包装器
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();

        // 判断是否指定了分类ID
        if (request.getClassId() != null) {
            // 如果分类ID为0
            if (request.getClassId() == 0) {
                // 按照阅读数量降序排序
                queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);

                // 调用mapPostList方法进行查询，并将结果赋值给appPage
                appPage = this.mapPostList(page, queryWrapper, "0");
            } else {
                // 添加分类ID等于指定值的查询条件
                queryWrapper.lambda().eq(PostEntity::getCut, request.getClassId());

                // 按照ID降序排序
                queryWrapper.lambda().orderByDesc(PostEntity::getId);

                // 调用mapPostList方法进行查询，并将结果赋值给appPage
                appPage = this.mapPostList(page, queryWrapper, "0");
            }
        } else {
            // 判断是否指定了圈子ID
            if (ObjectUtil.isNotNull(request.getTopicId())) {
                // 添加话题ID等于指定值的查询条件
                queryWrapper.lambda().eq(PostEntity::getTopicId, request.getTopicId());
            }

            // 判断是否指定了排序方式
            if (ObjectUtil.isNotNull(request.getOrder())) {
                // 如果排序方式为按照阅读数量降序
                if (request.getOrder().equals(Constant.ORDER_DESC_READCOUNT)) {
                    // 按照阅读数量降序排序
                    queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);
                }
                // 如果排序方式为按照ID降序
                else if (request.getOrder().equals(Constant.ORDER_DESC_ID)) {
                    // 按照ID降序排序
                    queryWrapper.lambda().orderByDesc(PostEntity::getId);
                }
            } else {
                // 按照"post_top"和"id"字段降序排序
                queryWrapper.orderByDesc("post_top", "id");
            }

            // 判断是否指定了用户ID
            if (ObjectUtil.isNotNull(request.getUid())) {
                // 添加用户ID等于指定值的查询条件
                queryWrapper.lambda().eq(PostEntity::getUid, request.getUid());
                //这里为什么要设置话题id为0
                // queryWrapper.lambda().eq(PostEntity::getDiscussId,0);

                // 调用mapPostList方法进行查询，并将结果赋值给appPage
                appPage = this.mapPostList(page, queryWrapper, request.getUid());
            } else {
                // 调用mapPostList方法进行查询，并将结果赋值给appPage
                appPage = this.mapPostList(page, queryWrapper, uid);
            }
        }

        // 返回查询结果
        return appPage;
    }


    /**
     * 组装帖子分页
     * 在一个循环里 尽量减少数据库查询操作 这种方式并不太好 应该全部查询出来后再set值
     *
     * @param page
     * @param queryWrapper
     * @param uid
     * @return
     */
    public AppPageUtils mapPostList(Page<PostEntity> page, QueryWrapper<PostEntity> queryWrapper, String uid) {
        // 从数据库中查询符合条件(根据"post_top", "id"降序的全部帖子)的帖子列表，并返回分页结果
        Page<PostEntity> pages = baseMapper.selectPage(page, queryWrapper);
        // 将分页结果封装到AppPageUtils对象中
        AppPageUtils appPage = new AppPageUtils(pages);
        // 获取帖子列表数据
        List<PostEntity> data = (List<PostEntity>) appPage.getData();
        // 创建用于存储响应对象的列表
        List<PostListResponse> responseList = new ArrayList<>();

        // 遍历每个帖子实体，进行相应的处理
        data.forEach(l -> {
            // 创建一个响应对象
            PostListResponse response = new PostListResponse();
            // 将帖子实体的属性复制到响应对象中
            BeanUtils.copyProperties(l, response);
            // 设置收藏数
            response.setCollectionCount(postCollectionService.collectCount(response.getId()));
            // 设置评论数
            response.setCommentCount(commentService.getCountByTopicId(response.getId()));
            // 设置用户信息
            Member userInfo = memberService.getById(response.getUid());
            if (userInfo == null) {
                userInfo = new Member();
                Robot robotInfo = robotServiceImpl.getById(response.getUid());
                userInfo.setUsername(robotInfo.getUsername());
                userInfo.setStudentId(robotInfo.getStudentId());
                userInfo.setNickName(robotInfo.getNickName());
                userInfo.setSex(robotInfo.getSex());
                userInfo.setBirthday(robotInfo.getBirthday());
                userInfo.setRegionId(robotInfo.getRegionId());
                userInfo.setRegion(robotInfo.getRegion());
                userInfo.setMobile(robotInfo.getMobile());
                userInfo.setPoint(robotInfo.getPoint());
                userInfo.setTotalPoint(robotInfo.getTotalPoint());
                userInfo.setFace(robotInfo.getFace());
                userInfo.setDisabled(robotInfo.getDisabled());
                userInfo.setHaveStore(robotInfo.getHaveStore());
                userInfo.setStoreId(robotInfo.getStoreId());
                userInfo.setClientEnum(robotInfo.getClientEnum());
                userInfo.setLastLoginDate(robotInfo.getLastLoginDate());
                userInfo.setGradeId(robotInfo.getGradeId());
                userInfo.setExperience(robotInfo.getExperience());
                userInfo.setTenantIds(robotInfo.getTenantIds());
            }
            response.setUserInfo(userInfo);
            response.setLevel(getUserLevel(userInfo.getTotalPoint()));

            // 判断用户是否为匿名用户（uid为 "0"），如果是则设置isCollection为false，否则根据用户和帖子id判断该帖子是否被当前用户收藏
            if ("0".equals(uid)) {
                response.setIsCollection(false);
            } else {
                response.setIsCollection(postCollectionService.isCollection(uid, response.getId()));
            }

            // 解析帖子图片并设置到响应对象中
            response.setMedia(JsonUtils.JsonToList(l.getMedia()));

            // 解析标签分类数据并设置到响应对象中
            List<Integer> cateIdList = JSON.parseArray(l.getCut(), Integer.class);
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


    /**
     * 组装帖子分页
     * 在一个循环里 尽量减少数据库查询操作 这种方式并不太好 应该全部查询出来后再set值
     *
     * @param page
     * @param queryWrapper
     * @param uid
     * @return
     */
    public AppPageUtils mapLastPostList(Page<PostEntity> page, QueryWrapper<PostEntity> queryWrapper, String uid, Integer classId) {
        // 从数据库中查询符合条件(根据"post_top", "id"降序的全部帖子)的帖子列表，并返回分页结果
        Page<PostEntity> pages = baseMapper.selectPage(page, queryWrapper);
        // 将分页结果封装到AppPageUtils对象中
        AppPageUtils appPage = new AppPageUtils(pages);
        // 获取帖子列表数据
        List<PostEntity> data = (List<PostEntity>) appPage.getData();
        // 创建用于存储响应对象的列表
        List<PostListResponse> responseList = new ArrayList<>();

        // 遍历每个帖子实体，进行相应的处理
        data.forEach(l -> {
            // 获取帖子的分类列表
            List<Integer> cateIdList = JSON.parseArray(l.getCut(), Integer.class);

            // 根据传入的classId获取对应分类下的帖子
            // 判断当前帖子是否属于指定的分类，或者classId为0（即不限制分类）
            if (cateIdList.contains(classId) || classId == 0) {
                // 创建一个响应对象
                PostListResponse response = new PostListResponse();
                // 将帖子实体的属性复制到响应对象中
                BeanUtils.copyProperties(l, response);
                // 设置收藏数
                response.setCollectionCount(postCollectionService.collectCount(response.getId()));
                // 设置评论数
                response.setCommentCount(commentService.getCountByTopicId(response.getId()));
                // 设置用户信息
                Member userInfo = memberService.getById(response.getUid());
                System.out.println("useruseruser" + userInfo);
                if (userInfo == null) {
                    userInfo = new Member();
                    Robot robotInfo = robotServiceImpl.getById(response.getUid());
                    userInfo.setUsername(robotInfo.getUsername());
                    userInfo.setStudentId(robotInfo.getStudentId());
                    userInfo.setNickName(robotInfo.getNickName());
                    userInfo.setSex(robotInfo.getSex());
                    userInfo.setBirthday(robotInfo.getBirthday());
                    userInfo.setRegionId(robotInfo.getRegionId());
                    userInfo.setRegion(robotInfo.getRegion());
                    userInfo.setMobile(robotInfo.getMobile());
                    userInfo.setPoint(robotInfo.getPoint());
                    userInfo.setTotalPoint(robotInfo.getTotalPoint());
                    userInfo.setFace(robotInfo.getFace());
                    userInfo.setDisabled(robotInfo.getDisabled());
                    userInfo.setHaveStore(robotInfo.getHaveStore());
                    userInfo.setStoreId(robotInfo.getStoreId());
                    userInfo.setClientEnum(robotInfo.getClientEnum());
                    userInfo.setLastLoginDate(robotInfo.getLastLoginDate());
                    userInfo.setGradeId(robotInfo.getGradeId());
                    userInfo.setExperience(robotInfo.getExperience());
                    userInfo.setTenantIds(robotInfo.getTenantIds());
                    System.out.println("robotrobotrobot" + userInfo);
                }
                response.setUserInfo(userInfo);
                // 设置用户等级
                response.setLevel(getUserLevel(userInfo.getTotalPoint()));

                // 判断用户是否为匿名用户（uid为 "0"），如果是则设置isCollection为false，否则根据用户和帖子id判断是否已收藏
                if ("0".equals(uid)) {
                    response.setIsCollection(false);
                } else {
                    response.setIsCollection(postCollectionService.isCollection(uid, response.getId()));
                }

                // 解析媒体数据并设置到响应对象中
                response.setMedia(JsonUtils.JsonToList(l.getMedia()));

                // 解析分类数据并设置到响应对象中
                List<String> cateNameList = new ArrayList<>();
                for (Integer cateId : cateIdList) {
                    CategoryEntity category = categoryService.getById(cateId);
                    cateNameList.add(category.getCateName());
                }
                response.setCutName(cateNameList);

                // 将响应对象添加到列表中
                responseList.add(response);
            }
        });

        // 更新appPage的数据为响应对象列表
        appPage.setData(responseList);

        // 返回更新后的appPage对象
        return appPage;
    }


}
