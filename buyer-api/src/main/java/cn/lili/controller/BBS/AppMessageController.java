package cn.lili.controller.BBS;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.entity.vo.MessageCountResponse;
import cn.lili.modules.BBS.param.AddInviteForm;
import cn.lili.modules.BBS.service.*;
import cn.lili.modules.BBS.utils.AppPageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-07 20:18:15
 **/
@Api(tags = "用户端——消息")
@RestController
@RequestMapping("buyer/bbs/message")
public class AppMessageController {

    @Resource
    private CommentMessageService commentMessageService;
    @Resource
    private EndorseMessageService endorseMessageService;
    @Resource
    private CollectMessageService collectMessageService;
    @Resource
    private FollowMessageService followMessageService;
    @Resource
    private InviteMessageService inviteMessageService;

    @GetMapping("/comment/{currPage}")
    @ApiOperation("评论消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R commentMessageList(@PathVariable Integer currPage) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = commentMessageService.getCommentMessages(currPage,authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/comment/updateCommentMessagesAsRead")
    @ApiOperation("批量更新评论消息为已读")
    public R updateCommentMessagesAsRead(@RequestBody List<Long> messageIds) {
        commentMessageService.markMessagesAsRead(messageIds);
        return R.ok();
    }


    @GetMapping("/messageNoReadCount")
    @ApiOperation("未读评论消息数量")
    public R messageNoReadCount() {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        MessageCountResponse data = commentMessageService.getMessageNoReadCount(authUser.getId());
        return R.ok().put("result", data);
    }

    @GetMapping("/allMessageNoReadCount")
    @ApiOperation("未读评论消息总数量")
    public R allMessageNoReadCount() {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        Integer data = commentMessageService.getAllMessageNoReadCount(authUser.getId());
        return R.ok().put("result", data);
    }


    @GetMapping("/endorse/{currPage}")
    @ApiOperation("赞同喜欢消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R endorseMessageList(@PathVariable Integer currPage) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = endorseMessageService.getEndorseMessages(currPage,authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/endorse/updateEndorseMessagesAsRead")
    @ApiOperation("批量更新赞同喜欢消息为已读")
    public R updateEndorseMessagesAsRead(@RequestBody List<Long> messageIds) {
        endorseMessageService.markMessagesAsRead(messageIds);
        return R.ok();
    }


    @GetMapping("/collect/{currPage}")
    @ApiOperation("收藏了我消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R collectMessageList(@PathVariable Integer currPage) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = collectMessageService.getCollectMessages(currPage,authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/collect/updateCollectMessagesAsRead")
    @ApiOperation("批量更新收藏了我消息为已读")
    public R updateCollectMessagesAsRead(@RequestBody List<Long> messageIds) {
        collectMessageService.markMessagesAsRead(messageIds);
        return R.ok();
    }

    @GetMapping("/follow/{currPage}")
    @ApiOperation("关注了我消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R followMessageList(@PathVariable Integer currPage) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = followMessageService.getFollowMessages(currPage,authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/follow/updateFollowMessagesAsRead")
    @ApiOperation("批量更新关注了我消息为已读")
    public R updateFollowMessagesAsRead(@RequestBody List<Long> messageIds) {
       followMessageService.markMessagesAsRead(messageIds);
        return R.ok();
    }

    @GetMapping("/invite/{currPage}")
    @ApiOperation("邀请回答消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页", paramType = "query", dataType = "Integer", required = true)
    })
    public R inviteMessageList(@PathVariable Integer currPage) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = inviteMessageService.getInviteMessages(currPage,authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/invite/updateInviteMessagesAsRead")
    @ApiOperation("批量更新邀请回答消息为已读")
    public R updateInviteMessagesAsRead(@RequestBody List<Long> messageIds) {
        inviteMessageService.markMessagesAsRead(messageIds);
        return R.ok();
    }

    @PostMapping("/inviteReply")
    @ApiOperation("邀请回答")
    public R inviteReply(@RequestBody AddInviteForm addInviteForm) {
        AuthUser authUser = UserContext.getCurrentUser();
        if (authUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        inviteMessageService.addInviteMessage(addInviteForm,authUser.getId());
        return R.ok();
    }

}
