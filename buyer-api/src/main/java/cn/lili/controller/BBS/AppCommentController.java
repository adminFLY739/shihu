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



import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.service.CommentService;
import cn.lili.modules.BBS.service.CommentThumbsService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author linfeng
 * @date 2022/7/27 15:35
 *
 */
@Api(tags = "用户端——评论")
@RestController
@RequestMapping("buyer/bbs/comment")
public class AppCommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentThumbsService commentThumbsService;

    /**
     * 评论列表
     */
    @GetMapping("/list/{postId}/{page}")
    @ApiOperation("评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "帖子id", paramType = "query",dataType = "Integer", required = true),
            @ApiImplicitParam(name = "page", value = "分页页码",paramType = "query", dataType = "Integer", required = true)
    })
    public R list(@PathVariable("postId")Integer postId, @PathVariable("page")Integer page){
        AppPageUtils pages = commentService.queryCommentPage(postId,page);

        return R.ok().put("result", pages);
    }


    /**
     * 评论区的点赞
     */
    @PostMapping("/thumbs")
    @ApiOperation("评论区的点赞")
    public R thumbs(@RequestBody AddThumbsForm request){
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }

        commentThumbsService.addThumbs(request,authUser.getId());
        return R.ok();
    }

    /**
     * 取消评论区的点赞
     */
    @PostMapping("/cancelThumbs")
    @ApiOperation("取消评论区的点赞")
    public R cancelThumbs(@RequestBody AddThumbsForm request){
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        commentThumbsService.cancelThumbs(request,authUser.getId());
        return R.ok();
    }
}
