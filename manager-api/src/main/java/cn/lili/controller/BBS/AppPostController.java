/**
 * -----------------------------------
 * 林风社交论坛开源版本请务必保留此注释头信息
 * 开源地址: https://gitee.com/virus010101/linfeng-community
 * 可正常分享和学习源码，不得用于非法牟利！
 * 商业版购买联系技术客服 QQ: 3582996245
 * Copyright (c) 2021-2023 linfeng all rights reserved.
 * 演示站点:https://www.linfeng.tech
 * 版权所有，侵权必究！
 * -----------------------------------
 */
package cn.lili.controller.BBS;

import cn.hutool.core.util.ObjectUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.entity.vo.PostDetailResponse;
import cn.lili.modules.BBS.param.*;
import cn.lili.modules.BBS.service.PostCollectionService;
import cn.lili.modules.BBS.service.PostOpposeService;
import cn.lili.modules.BBS.service.PostService;
import cn.lili.modules.BBS.service.PostThumbService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author linfeng
 * @date 2022/7/27 14:18
 */
@Api(tags = "用户端——帖子")
@RestController
@RequestMapping("manager/bbs/post")
public class AppPostController {

    @Autowired
    private PostService postService;
    @Autowired
    private PostCollectionService postCollectionService;
    @Resource
    private PostThumbService postThumbService;
    @Resource
    private PostOpposeService postOpposeService;


    @GetMapping("/lastPost/{currPage}/{classId}")
    @ApiOperation("最新动态列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R lastPost(@PathVariable Integer currPage, @PathVariable Integer classId) {
        AppPageUtils pages = postService.lastPost(currPage, classId);
        return R.ok().put("result", pages);
    }


    @GetMapping("/followUserPost/{page}")
    @ApiOperation("获取关注用户帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R followUserPost(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = postService.followUserPost(page, authUser.getId());
        if (ObjectUtil.isNull(page)) {
            return R.error("您没有关注的用户");
        }
        return R.ok().put("result", pages);
    }


    /**
     * 帖子收藏
     */
    @PostMapping("/addCollection")
    @ApiOperation("帖子收藏")
    public R addCollection(@RequestBody AddCollectionForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postService.addCollection(request, authUser.getId());

        return R.ok();
    }

    /**
     * 帖子取消收藏
     */
    @PostMapping("/cancelCollection")
    @ApiOperation("帖子取消收藏")
    public R cancelCollection(@RequestBody AddCollectionForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postCollectionService.cancelCollection(request, authUser.getId());
        return R.ok();
    }

    /**
     * 帖子点赞
     */
    @PostMapping("/addPostThumb")
    @ApiOperation("帖子点赞")
    public R addPostThumb(@RequestBody AddPostThumbForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postThumbService.addPostThumb(request, authUser.getId());

        return R.ok();
    }

    /**
     * 帖子取消点赞
     */
    @PostMapping("/cancelPostThumb")
    @ApiOperation("帖子取消点赞")
    public R cancelPostThumb(@RequestBody AddPostThumbForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postThumbService.cancelPostThumb(request, authUser.getId());
        return R.ok();
    }

    /**
     * 帖子反对
     */
    @PostMapping("/addPostOppose")
    @ApiOperation("帖子反对")
    public R addPostOppose(@RequestBody AddPostOpposeForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postOpposeService.addPostOppose(request, authUser.getId());

        return R.ok();
    }

    /**
     * 帖子取消反对
     */
    @PostMapping("/cancelPostOppose")
    @ApiOperation("帖子取消反对")
    public R cancelPostOppose(@RequestBody AddPostOpposeForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postOpposeService.cancelPostOppose(request, authUser.getId());
        return R.ok();
    }


    @GetMapping("/myPost/{page}")
    @ApiOperation("我的帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R myPost(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = postService.myPost(page, authUser.getId());
        return R.ok().put("result", pages);
    }


    @GetMapping("/myCollectPost/{page}")
    @ApiOperation("我收藏的帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R myCollectPost(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }

        AppPageUtils pages = postService.myCollectPost(page, authUser.getId());
        return R.ok().put("result", pages);
    }


    @GetMapping("/detail/{id}")
    @ApiOperation("获取帖子详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "帖子id", paramType = "query", dataType = "Integer", required = true)
    })
    public R detail(@PathVariable Integer id) {

        PostDetailResponse response = postService.detail(id);
        return R.ok().put("result", response);
    }


    @PostMapping("/addComment")
    @ApiOperation("添加评论")
    public R addComment(@RequestBody AddCommentForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        ValidatorUtils.validateEntity(request);
        postService.addComment(request, authUser.getId());
        return R.ok();
    }

    @PostMapping("/delComment")
    @ApiOperation("删除评论")
    public R delComment(@RequestBody DelCommentForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        postService.delComment(request, authUser.getId());

        return R.ok();
    }


    @PostMapping("/addPost")
    @ApiOperation("发帖子")
    public R addPost(@RequestBody AddManagerPostForm request) {
//        AuthUser authUser = UserContext.getCurrentUser();
//        if (authUser == null) {
//            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
//        }
//        ValidatorUtils.validateEntity(request);
        Integer id = postService.addManagerPost(request);
        if (id == 0) {
            return R.error("发帖失败");
        }
        return R.ok().put("result", id);
    }


    @PostMapping("/delPost")
    @ApiOperation("删除帖子")
    public R delPost(@RequestBody AddCollectionForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        ValidatorUtils.validateEntity(request);
        postService.delPost(request, authUser.getId());
        return R.ok();
    }

    @PostMapping("/updatePost")
    @ApiOperation("更新帖子")
    public R updatePost(@RequestBody AddPostForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        ValidatorUtils.validateEntity(request);
        Integer id = postService.updatePost(request, authUser.getId());
        return R.ok().put("result", id);
    }


    @PostMapping("/list")
    @ApiOperation("帖子列表分页")
    public R list(@RequestBody PostListForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }

        AppPageUtils page = postService.queryPageList(request, authUser.getId());

        return R.ok().put("result", page);
    }
}
