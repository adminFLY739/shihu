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
import cn.lili.modules.BBS.entity.vo.*;
import cn.lili.modules.BBS.mapper.AppUserDao;
import cn.lili.modules.BBS.mapper.FollowDao;
import cn.lili.modules.BBS.param.AddFollowForm;
import cn.lili.modules.BBS.param.UserRecommendListForm;
import cn.lili.modules.BBS.service.*;
import cn.lili.modules.BBS.utils.AppPageUtils;
import cn.lili.modules.BBS.utils.DateUtil;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.enums.PointTypeEnum;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.robot.serviceImpl.RobotServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class AppUserServiceImpl extends ServiceImpl<AppUserDao, AppUserEntity> implements AppUserService {


    @Autowired
    private PostService postService;
    @Autowired
    private AppUserDao userDao;
//    @Autowired
//    private RedisUtils redisUtils;
    @Autowired
    private FollowService followService;
    @Autowired
    private FollowDao followDao;
    @Autowired
    private MemberService memberService;
    @Resource
    private InviteMessageService inviteMessageService;
    @Resource
    private FollowMessageService followMessageService;
    @Resource
    private TaskUserService taskUserService;
    @Autowired
    private RobotServiceImpl robotServiceImpl;

//    @Autowired
//    private SystemService systemService;
//
//    @Autowired
//    private CommentService commentService;


//    @Override
//    public PageUtils queryPage(Map<String, Object> params) {
//        QueryWrapper<AppUserEntity> queryWrapper = new QueryWrapper<>();
//        //模糊查询
//        String key = (String) params.get("key");
//        if (!ObjectUtil.isEmpty(key)) {
//            queryWrapper.like("username", key)
//                    .or()
//                    .like("mobile", key);
//        }
//        queryWrapper.lambda().orderByDesc(AppUserEntity::getUid);
//        IPage<AppUserEntity> page = this.page(
//                new Query<AppUserEntity>().getPage(params),
//                queryWrapper
//        );
//        return new PageUtils(page);
//    }
//
//
//    @Override
//    public void ban(Integer id) {
//        Integer status = this.lambdaQuery().eq(AppUserEntity::getUid, id).one().getStatus();
//        if (status.equals(Constant.USER_BANNER)) {
//            throw new LinfengException("该用户已被禁用");
//        }
//        this.lambdaUpdate()
//                .set(AppUserEntity::getStatus, 1)
//                .eq(AppUserEntity::getUid, id)
//                .update();
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void openBan(Integer id) {
//        Integer status = this.lambdaQuery().eq(AppUserEntity::getUid, id).one().getStatus();
//        if (status.equals(Constant.USER_NORMAL)) {
//            throw new LinfengException("该用户已解除禁用");
//        }
//        boolean update = this.lambdaUpdate()
//                .set(AppUserEntity::getStatus, 0)
//                .eq(AppUserEntity::getUid, id)
//                .update();
//        if(!update){
//            throw new LinfengException("解除失败");
//        }
//    }

//    @Override
//    public HomeRateResponse indexDate() {
//        String today = cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd");
//        String yesterday = cn.hutool.core.date.DateUtil.yesterday().toString("yyyy-MM-dd");
//        Integer postCount = postService.lambdaQuery().select(PostEntity::getId).count();
//        HomeRateResponse response = new HomeRateResponse();
//        response.setTotalPostOfReview(0);
//        response.setTotalPost(postCount);
//        response.setNewUserNum(this.getRegisterNumByDate(today));
//        response.setYesterdayNewUserNum(this.getRegisterNumByDate(yesterday));
//        response.setTotalUser(this.getTotalNum());
//        response.setYesterdayCommentCount(commentService.getYesterdayCount());
//        response.setCommentCount(commentService.getAllCount());
//        return response;
//    }

//    @Override
//    public Integer smsLogin(SmsLoginForm form, HttpServletRequest request) {
//        AppUserEntity appUserEntity = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
//        String codeKey = "code_" + form.getMobile();
//        String s = redisUtils.get(codeKey);
//        if (!s.equals(form.getCode())) {
//            throw new LinfengException("验证码错误");
//        }
//        if (ObjectUtil.isNotNull(appUserEntity)) {
//            //登录
//            if (appUserEntity.getStatus() == 1) {
//                throw new LinfengException("该账户已被禁用");
//            }
//            return appUserEntity.getUid();
//        } else {
//            //注册
//            AppUserEntity appUser = new AppUserEntity();
//            appUser.setMobile(form.getMobile());
//            appUser.setGender(0);
//            appUser.setAvatar(Constant.DEAULT_HEAD);
//            appUser.setUsername("LF_" + RandomUtil.randomNumbers(8));
//            appUser.setCreateTime(DateUtil.nowDateTime());
//            appUser.setUpdateTime(DateUtil.nowDateTime());
//            List<String> list = new ArrayList<>();
//            list.add("新人");
//            appUser.setTagStr(JSON.toJSONString(list));
//            baseMapper.insert(appUser);
//            AppUserEntity user = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
//            return user.getUid();
//        }
//
//
//    }

//    @Override
//    public String sendSmsCode(SendCodeForm param) {
//        String code = RandomUtil.randomNumbers(6);
//        String codeKey = "code_" + param.getMobile();
//        String s = redisUtils.get(codeKey);
//        if (ObjectUtil.isNotNull(s)) {
//            return s;
//        }
//        redisUtils.set(codeKey, code, 60 * 5);
//        return code;
//    }

    @Override
    public AppUserResponse getUserInfo(String uid) {

        AppUserResponse response = new AppUserResponse();
        Member member = memberService.getById(uid);
        BeanUtils.copyProperties(member, response);
        Integer follow = followService.getFollowCount(uid);
        Integer fans = followService.getFans(uid);
        Integer postNum = postService.getPostNumByUid(uid);
        response.setFans(fans);
        response.setPostNum(postNum);
        response.setFollow(follow);
        return response;
    }

//    @Override
//    public void updateAppUserInfo(AppUserUpdateForm appUserUpdateForm, AppUserEntity user) {
//        if (!ObjectUtil.isEmpty(appUserUpdateForm.getAvatar())) {
//            user.setAvatar(appUserUpdateForm.getAvatar());
//        }
//        if(!ObjectUtil.isEmpty(appUserUpdateForm.getGender())){
//            user.setGender(appUserUpdateForm.getGender());
//        }
//        baseMapper.updateById(user);
//        redisUtils.delete("userId:" + user.getUid());
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFollow(AddFollowForm request, String uid) {
        if (request.getId().equals(uid)) {
            throw new LinfengException("不能关注自己哦");
        }
        boolean isFollow = followService.isFollowOrNot(uid, request.getId());
        if (isFollow) {
            throw new LinfengException("不要重复关注哦");
        }
        FollowEntity followEntity = new FollowEntity();
        followEntity.setUid(uid);
        followEntity.setFollowUid(request.getId());
        followEntity.setCreateTime(DateUtil.nowDateTime());
        followService.save(followEntity);

        // 消息通知
        FollowMessageEntity followMessageEntity = new FollowMessageEntity();
        followMessageEntity.setUid(uid);
        followMessageEntity.setReceiverUid(request.getId());
        followMessageEntity.setCreateTime(DateUtil.nowDateTime());
        followMessageEntity.setIsRead(Boolean.FALSE);
        followMessageService.save(followMessageEntity);

        // 每日任务监测
        taskUserService.addTaskUser(uid,"4",3L,"关注 1 位感兴趣的用户");
    }

    @Override
    public void cancelFollow(AddFollowForm request, String uid) {
        followDao.cancelFollow(uid, request.getId());
    }

    @Override
    public AppPageUtils userFans(Integer currPage, String uid) {
        List<String> uidList = followService.getFansList(uid);
        if (uidList.isEmpty()) {
            return new AppPageUtils(uidList, 0, 10, currPage);
        }
        Page<Member> page = new Page<>(currPage, 10);
        QueryWrapper<Member> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(Member::getId, uidList);
        Page<Member> page1 = memberService.page(page, queryWrapper1);

        AppPageUtils pages = new AppPageUtils(page1);
        List<?> data = pages.getData();
        List<TopicUserResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            TopicUserResponse topicUserResponse = new TopicUserResponse();
            BeanUtils.copyProperties(l, topicUserResponse);
            Integer follow = followService.isFollow(uid, topicUserResponse.getId());
            topicUserResponse.setHasFollow(follow);
            responseList.add(topicUserResponse);
        });
        pages.setData(responseList);
        return pages;
    }

    @Override
    public AppPageUtils follow(Integer currPage, String uid) {
        List<String> followUids = followService.getFollowUids(uid);
        if (followUids.isEmpty()) {
            return new AppPageUtils(followUids, 0, 10, currPage);
        }
        Page<Member> page = new Page<>(currPage, 10);
        QueryWrapper<Member> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(Member::getId, followUids);
        Page<Member> page1 = memberService.page(page, queryWrapper1);


        AppPageUtils pages = new AppPageUtils(page1);
        List<Member> data = (List<Member>) pages.getData();
        List<TopicUserResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            TopicUserResponse topicUserResponse = new TopicUserResponse();
            BeanUtils.copyProperties(l, topicUserResponse);
            Integer follow = followService.isFollow(uid, topicUserResponse.getId());
            topicUserResponse.setHasFollow(follow);
            topicUserResponse.setLevel(getUserLevel(l.getTotalPoint()));
            responseList.add(topicUserResponse);
        });
        pages.setData(responseList);
        return pages;
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
    public AppUserInfoResponse findUserInfoById(String uid) {
       Member member = memberService.getById(uid);
       if (member == null) {
           member = new Member();
           Robot robotInfo = robotServiceImpl.getById(uid);
           member.setId(robotInfo.getId());
           member.setUsername(robotInfo.getUsername());
           member.setStudentId(robotInfo.getStudentId());
           member.setNickName(robotInfo.getNickName());
           member.setSex(robotInfo.getSex());
           member.setBirthday(robotInfo.getBirthday());
           member.setRegionId(robotInfo.getRegionId());
           member.setRegion(robotInfo.getRegion());
           member.setMobile(robotInfo.getMobile());
           member.setPoint(robotInfo.getPoint());
           member.setTotalPoint(robotInfo.getTotalPoint());
           member.setFace(robotInfo.getFace());
           member.setDisabled(robotInfo.getDisabled());
           member.setHaveStore(robotInfo.getHaveStore());
           member.setStoreId(robotInfo.getStoreId());
           member.setClientEnum(robotInfo.getClientEnum());
           member.setLastLoginDate(robotInfo.getLastLoginDate());
           member.setGradeId(robotInfo.getGradeId());
           member.setExperience(robotInfo.getExperience());
           member.setTenantIds(robotInfo.getTenantIds());
       }
        if(ObjectUtil.isNull(member)){
            throw new LinfengException("用户不存在");
        }
        AppUserInfoResponse response = new AppUserInfoResponse();
        BeanUtils.copyProperties(member, response);
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        boolean isFollow = followService.isFollowOrNot(authUser.getId(),uid);
        response.setIsFollow(isFollow);
        return response;
    }

    @Override
    public AppPageUtils getUserRecommendList(UserRecommendListForm request, String uid) {
        Page<Member> page = new Page<>(request.getCurrPage(), 10);
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("tenant_ids",request.getTenantId());
        // queryWrapper.eq("disabled",true);
        Page<Member> pages = memberService.page(page, queryWrapper);
        AppPageUtils appPage = new AppPageUtils(pages);

        List<Member> data = (List<Member>) appPage.getData();
        // 创建用于存储响应对象的列表
        List<UserRecommendListResponse> responseList = new ArrayList<>();

        data.forEach( item -> {
            UserRecommendListResponse response = new UserRecommendListResponse();

            // 查询是否已邀请
            QueryWrapper<InviteMessageEntity> query = new QueryWrapper<>();
            query.eq("uid",uid);
            query.eq("receiver_uid",item.getId());
            query.eq("discuss_id",request.getDiscussId());
            long count = inviteMessageService.count(query);
            if (count > 0){
                response.setIsInvite(Boolean.TRUE);
            }else {
                response.setIsInvite(Boolean.FALSE);
            }

            response.setId(item.getId());
            response.setAvatar(item.getFace());
            response.setNickName(item.getNickName());
            response.setLevel(getUserLevel(item.getTotalPoint()));

            responseList.add(response);
        });

        // 更新appPage的数据为响应对象列表
        appPage.setData(responseList);

        return appPage;
    }
//
//    @Override
//    public Integer miniWxLogin(WxLoginForm form) {
//
//        String openId = getOpenId(form.getCode());
//        if(io.linfeng.common.utils.ObjectUtil.isEmpty(openId)){
//            throw new LinfengException("请正确配置appId和密钥");
//        }
//        //根据openId获取数据库信息 判断用户是否登录
//        AppUserEntity user = this.lambdaQuery().eq(AppUserEntity::getOpenid, openId).one();
//        if (ObjectUtil.isNotNull(user)) {
//            if (user.getStatus() == 1) {
//                throw new LinfengException("该账户已被禁用");
//            }
//            //其他业务todo
//            return user.getUid();
//        } else {
//            //新注册用户
//            AppUserEntity appUser = new AppUserEntity();
//            appUser.setGender(0);
//            appUser.setAvatar(Constant.DEAULT_HEAD);
//            appUser.setUsername("LF_wx" + RandomUtil.randomNumbers(8));
//            appUser.setCreateTime(DateUtil.nowDateTime());
//            appUser.setUpdateTime(DateUtil.nowDateTime());
//            appUser.setOpenid(openId);
//            List<String> list = new ArrayList<>();
//            list.add("新人");
//            appUser.setTagStr(JSON.toJSONString(list));
//            baseMapper.insert(appUser);
//            AppUserEntity users = this.lambdaQuery().eq(AppUserEntity::getOpenid, openId).one();
//            return users.getUid();
//        }
//    }
//
//    @Override
//    public List<AppUserRankResponse> userRank() {
//        DateTime month = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
//
//        List<PostEntity> postList = postService.lambdaQuery().gt(PostEntity::getCreateTime, month).list();
//        if(postList.isEmpty()){
//            return new ArrayList<>();
//        }
//        Map<Integer, Long> collect = postList.stream().collect(Collectors.groupingBy(PostEntity::getUid, Collectors.counting()));
//        Map<Integer, Long> sorted = collect
//                .entrySet()
//                .stream()
//                .sorted(Collections.reverseOrder(comparingByValue()))
//                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
//                                LinkedHashMap::new));
//        List<AppUserRankResponse> list=new ArrayList<>();
//        sorted.forEach((k,v)->{
//            AppUserRankResponse response=new AppUserRankResponse();
//            BeanUtils.copyProperties(this.getById(k),response);
//            response.setPostNumber(v.intValue());
//            list.add(response);
//        });
//        return list;
//    }


//    private Integer getTotalNum() {
//        return this.lambdaQuery().select(AppUserEntity::getUid).count();
//    }
//
//    private Integer getRegisterNumByDate(String date) {
//        QueryWrapper<AppUserEntity> wrapper = Wrappers.query();
//        wrapper.select("uid");
//        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
//        return userDao.selectCount(wrapper);
//    }
//
//    private String getOpenId(String code){
//        SystemEntity system = systemService.lambdaQuery().eq(SystemEntity::getConfig, "miniapp").one();
//
//        //小程序唯一标识   (在微信小程序管理后台获取)
//        String appId = system.getValue();
//        //小程序的 app secret (在微信小程序管理后台获取)
//        String secret = system.getExtend();
//        //授权（必填）
//        String grant_type = "authorization_code";
//        //https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
//        //向微信服务器 使用登录凭证 code 获取 session_key 和 openid
//        String params = "appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=" + grant_type;
//        //发送请求
//        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
//        //解析相应内容（转换成json对象）
//        JSONObject json = JSON.parseObject(sr);
//        //用户的唯一标识（openId）
//        return (String) json.get("openid");
//    }

}
