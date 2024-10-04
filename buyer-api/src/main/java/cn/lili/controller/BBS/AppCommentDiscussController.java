package cn.lili.controller.BBS;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.service.CommentDiscussService;
import cn.lili.modules.BBS.service.CommentDiscussThumbsService;
import cn.lili.modules.BBS.service.CommentThumbsService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "用户端——话题评论")
@RestController
@RequestMapping("buyer/bbs/commentDiscuss")
public class AppCommentDiscussController {
    @Resource
    private CommentDiscussService commentDiscussService;

    @Resource
    private CommentDiscussThumbsService commentDiscussThumbsService;

    /**
     * 评论列表
     */
    @GetMapping("/list/{discussId}/{page}")
    @ApiOperation("评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "话题id", paramType = "query",dataType = "Integer", required = true),
            @ApiImplicitParam(name = "page", value = "分页页码",paramType = "query", dataType = "Integer", required = true)
    })
    public R list(@PathVariable("discussId")Integer discussId, @PathVariable("page")Integer page){
        AppPageUtils pages = commentDiscussService.queryCommentPage(discussId,page);
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

        commentDiscussThumbsService.addThumbs(request,authUser.getId());
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
        commentDiscussThumbsService.cancelThumbs(request,authUser.getId());
        return R.ok();
    }
}
