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
import cn.lili.modules.BBS.entity.vo.AppUserInfoResponse;
import cn.lili.modules.BBS.entity.vo.AppUserResponse;
import cn.lili.modules.BBS.param.AddFollowForm;
import cn.lili.modules.BBS.param.AppUserInfoForm;
import cn.lili.modules.BBS.param.UserRecommendListForm;
import cn.lili.modules.BBS.service.AppUserService;
import cn.lili.modules.BBS.utils.AppPageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * APP登录接口
 *
 * @author linfeng
 * @date 2022/6/9 22:40
 */
@RestController
@RequestMapping("buyer/bbs/user")
@Api(tags = "APP登录接口")
public class AppUserInfoController {


    @Autowired
    private AppUserService appUserService;


    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public R userInfo() {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppUserResponse response = appUserService.getUserInfo(authUser.getId());
        return R.ok().put("result", response);
    }

    @PostMapping("/cancelFollow")
    @ApiOperation("取消关注用户")
    public R cancelFollow(@RequestBody AddFollowForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        appUserService.cancelFollow(request, authUser.getId());
        return R.ok("取消关注用户成功");
    }

    @PostMapping("/addFollow")
    @ApiOperation("关注用户")
    public R addFollow(@RequestBody AddFollowForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        appUserService.addFollow(request, authUser.getId());
        return R.ok("关注用户成功");
    }


    @GetMapping("/userFans/{page}")
    @ApiOperation("我的粉丝分页列表")
    public R userFans(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }

        AppPageUtils pages = appUserService.userFans(page, authUser.getId());
        return R.ok().put("result", pages);
    }


    @GetMapping("/follow/{page}")
    @ApiOperation("我的关注分页列表")
    public R follow(@PathVariable Integer page) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = appUserService.follow(page, authUser.getId());
        return R.ok().put("result", pages);
    }

    @PostMapping("/userInfoById")
    @ApiOperation("用户个人主页信息")
    public R userInfoById(@RequestBody AppUserInfoForm request) {

        AppUserInfoResponse response = appUserService.findUserInfoById(request.getUid());

        return R.ok().put("result", response);
    }

    @PostMapping("/userRecommendList")
    @ApiOperation("推荐用户分页列表")
    public R userRecommendList(@RequestBody UserRecommendListForm request) {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        AppPageUtils pages = appUserService.getUserRecommendList(request,authUser.getId());
        return R.ok().put("result", pages);
    }

}
