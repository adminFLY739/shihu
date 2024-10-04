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
package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.AppUserEntity;
import cn.lili.modules.BBS.entity.vo.AppUserInfoResponse;
import cn.lili.modules.BBS.entity.vo.AppUserResponse;
import cn.lili.modules.BBS.param.AddFollowForm;
import cn.lili.modules.BBS.param.UserRecommendListForm;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-01-20 12:10:43
 */
public interface AppUserService extends IService<AppUserEntity> {

//    PageUtils queryPage(Map<String, Object> params);
//
//    void ban(Integer id);
//
//    void openBan(Integer id);

    /**
     * 首页数据
     * @return HomeRateResponse
     */
//    HomeRateResponse indexDate();
//
//    Integer smsLogin(SmsLoginForm form, HttpServletRequest request);
//
//    String sendSmsCode(SendCodeForm param);

    AppUserResponse getUserInfo(String uid);

//    void updateAppUserInfo(AppUserUpdateForm appUserUpdateForm, AppUserEntity user);

    void addFollow(AddFollowForm request, String uid);

    void cancelFollow(AddFollowForm request, String uid);

    AppPageUtils userFans(Integer page, String uid);

    AppPageUtils follow(Integer page, String uid);

    AppUserInfoResponse findUserInfoById(String uid);
//
//    Integer miniWxLogin(WxLoginForm form);
//
//    List<AppUserRankResponse> userRank();

    AppPageUtils getUserRecommendList(UserRecommendListForm request, String uid);
}

