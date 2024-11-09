package cn.lili.controller.BBS;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.entity.vo.DiscussDetailResponse;
import cn.lili.modules.BBS.param.*;
import cn.lili.modules.BBS.service.DiscussService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.validator.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "用户端——话题")
@RestController
@RequestMapping("manager/bbs/discuss")
public class AppDiscussController {

    @Resource
    private DiscussService discussService;

    @GetMapping("/lastDiscuss/{currPage}")
    @ApiOperation("最新话题列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R lastDiscuss(@PathVariable Integer currPage){
        AppPageUtils pages = discussService.lastDiscuss(currPage);
        return R.ok().put("result", pages);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取话题详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "话题id", paramType = "query", dataType = "Integer", required = true)
    })
    public R detail(@PathVariable Integer id) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        DiscussDetailResponse response = discussService.detail(id,authUser.getId());
        return R.ok().put("result", response);
    }

    @PostMapping("/addDiscuss")
    @ApiOperation("发布话题")
    public R addDiscuss(@RequestBody AddManagerDiscussForm addDiscussForm){
        Long id = discussService.addManagerDiscuss(addDiscussForm);
        if (id == 0) {
            return R.error("发布话题失败");
        }
        return R.ok().put("result", id);
    }

    @PostMapping("/updateDiscuss")
    @ApiOperation("更新话题")
    public R updateDiscuss(@RequestBody AddDiscussForm updateDiscussForm){
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        ValidatorUtils.validateEntity(updateDiscussForm);
        Long id = discussService.updateDiscuss(updateDiscussForm, authUser.getId());
        return R.ok().put("result", id);
    }

    @PostMapping("/addCommentDiscuss")
    @ApiOperation("添加话题评论")
    public R addComment(@RequestBody AddCommentDiscussForm addCommentDiscussForm) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        ValidatorUtils.validateEntity(addCommentDiscussForm);
        discussService.addCommentDiscuss(addCommentDiscussForm, authUser.getId());
        return R.ok();
    }

    @PostMapping("/delCommentDiscuss")
    @ApiOperation("删除评论")
    public R delComment(@RequestBody DelCommentDiscussForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        discussService.delCommentDiscuss(request, authUser.getId());
        return R.ok();
    }

    @PostMapping("/cancelFollowDiscuss")
    @ApiOperation("取消关注话题")
    public R cancelFollowDiscuss(@RequestBody AddFollowDiscussForm addFollowDiscussForm) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        discussService.cancelFollowDiscuss(addFollowDiscussForm, authUser.getId());
        return R.ok("取消关注话题成功");
    }


    @PostMapping("/addFollowDiscuss")
    @ApiOperation("关注话题")
    public R addFollowDiscuss(@RequestBody AddFollowDiscussForm addFollowDiscussForm) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        discussService.addFollowDiscuss(addFollowDiscussForm, authUser.getId());
        return R.ok("关注话题成功");
    }

    @GetMapping("/myFollowDiscuss/{page}")
    @ApiOperation("我关注的问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R myFollowDiscuss(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = discussService.myFollowDiscuss(page, authUser.getId());
        return R.ok().put("result", pages);
    }
}
