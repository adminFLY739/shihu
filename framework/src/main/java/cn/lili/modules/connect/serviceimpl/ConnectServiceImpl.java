package cn.lili.modules.connect.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.context.ThreadContextHolder;
import cn.lili.common.enums.ClientTypeEnum;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.event.TransactionCommitSendMQEvent;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.token.Token;
import cn.lili.common.utils.CookieUtil;
import cn.lili.common.utils.HttpUtils;
import cn.lili.modules.connect.entity.Connect;
import cn.lili.modules.connect.entity.dto.ConnectAuthUser;
import cn.lili.modules.connect.entity.dto.WechatMPLoginParams;
import cn.lili.modules.connect.entity.enums.ConnectEnum;
import cn.lili.modules.connect.mapper.ConnectMapper;
import cn.lili.modules.connect.service.ConnectService;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dto.ConnectQueryDTO;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.member.service.MemberTenantService;
import cn.lili.modules.member.token.MemberTokenGenerate;
import cn.lili.modules.statistics.entity.enums.ServiceTypeEnum;
import cn.lili.modules.statistics.service.ServiceStatisticsService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.connect.WechatConnectSetting;
import cn.lili.modules.system.entity.dto.connect.dto.WechatConnectSettingItem;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.NoPermissionException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 联合登陆接口实现
 *
 * @author Chopper
 */
@Slf4j
@Service
public class ConnectServiceImpl extends ServiceImpl<ConnectMapper, Connect> implements ConnectService {

    static final boolean AUTO_REGION = true;

    @Autowired
    private SettingService settingService;
    @Autowired
    private TenantAreaService tenantAreaService;

    @Autowired
    private MemberTenantService memberTenantService;

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberTokenGenerate memberTokenGenerate;
    @Autowired
    private Cache cache;
    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 接口调用统计
     */
    @Autowired
    private ServiceStatisticsService serviceStatisticsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Token unionLoginCallback(String type, String unionid, String uuid, boolean longTerm) throws NoPermissionException {

        try {
            LambdaQueryWrapper<Connect> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Connect::getUnionId, unionid);
            queryWrapper.eq(Connect::getUnionType, type);
            //查询绑定关系
            Connect connect = this.getOne(queryWrapper);
            if (connect == null) {
                throw new NoPermissionException("未绑定用户");
            }
            //查询会员
            Member member = memberService.getById(connect.getUserId());
            //如果未绑定会员，则把刚才查询到的联合登录表数据删除
            if (member == null) {
                this.remove(queryWrapper);
                throw new NoPermissionException("未绑定用户");
            }
            return memberTokenGenerate.createToken(member, longTerm);
        } catch (NoPermissionException e) {
            log.error("联合登陆失败：", e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Token unionLoginCallback(String type, ConnectAuthUser authUser, String uuid) {

        Token token;
        try {
            token = this.unionLoginCallback(type, authUser.getUuid(), uuid, false);
        } catch (NoPermissionException e) {
            if (AUTO_REGION) {
                token = memberService.autoRegister(authUser);
                return token;
            } else {
                //写入cookie
                CookieUtil.addCookie(CONNECT_COOKIE, uuid, 1800, ThreadContextHolder.getHttpResponse());
                CookieUtil.addCookie(CONNECT_TYPE, type, 1800, ThreadContextHolder.getHttpResponse());
                //自动登录失败，则把信息缓存起来
                cache.put(ConnectService.cacheKey(type, uuid), authUser, 30L, TimeUnit.MINUTES);
                throw new ServiceException(ResultCode.USER_NOT_BINDING);
            }
        } catch (Exception e) {
            log.error("联合登陆异常：", e);
            throw new ServiceException(ResultCode.ERROR);
        }
        return token;
    }

    @Override
    public void bind(String unionId, String type) {
        AuthUser authUser = Objects.requireNonNull(UserContext.getCurrentUser());
        Connect connect = new Connect(authUser.getId(), unionId, type);
        this.save(connect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(String type) {

        LambdaQueryWrapper<Connect> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Connect::getUserId, UserContext.getCurrentUser().getId());
        queryWrapper.eq(Connect::getUnionType, type);

        this.remove(queryWrapper);
    }

    @Override
    public List<String> bindList() {
        LambdaQueryWrapper<Connect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Connect::getUserId, UserContext.getCurrentUser().getId());
        List<Connect> connects = this.list(queryWrapper);
        List<String> keys = new ArrayList<>();
        connects.forEach(item -> keys.add(item.getUnionType()));
        return keys;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Token appLoginCallback(ConnectAuthUser authUser, String uuid) {
        try {
            return this.unionLoginCallback(authUser.getSource(), authUser.getUuid(), uuid, true);
        } catch (NoPermissionException e) {
            return memberService.autoRegister(authUser);
        }
    }


    /**
     * 微信小程序自动登录
     * @param params 小程序登录参数，其中包括uuid、code和appid等信息
     * @return 返回登录所需的Token
     */
    @Override
    @Transactional
    public Token miniProgramAutoLogin(WechatMPLoginParams params) {

        // 从缓存中获取微信联合登陆信息
        Object cacheData = cache.get(CachePrefix.WECHAT_SESSION_PARAMS.getPrefix() + params.getUuid());
        Map<String, String> map = new HashMap<>(3);
        if (cacheData == null) {
            // 如果缓存中没有相应信息，调用 getConnect 方法获取微信联合登陆信息
            JSONObject json = this.getConnect(params.getCode(),params.getAppid());

            // 从返回的结果中获取 session_key、unionid 和 openid
            String sessionKey = json.getStr("session_key");
            String unionId = json.getStr("unionid");
            String openId = json.getStr("openid");

            // 将获取到的信息存入 map 中，并将其存储到缓存中
            map.put("sessionKey", sessionKey);
            map.put("unionId", unionId);
            map.put("openId", openId);
            // 缓存十五分钟（即十五分钟内免登录）
            cache.put(CachePrefix.WECHAT_SESSION_PARAMS.getPrefix() + params.getUuid(), map, 900L);
        } else {
            // 如果缓存中存在相应信息，直接将其转换成 map
            map = (Map<String, String>) cacheData;
        }

        // 调用 phoneMpBindAndLogin 方法进行手机与小程序的绑定，并返回 Token
        return phoneMpBindAndLogin(map.get("sessionKey"), params, map.get("openId"), map.get("unionId"),params.getAppid());
    }

    /**
     * 杭师二手微信小程序自动登录
     * @param params 小程序登录参数，其中包括uuid、code和appid等信息
     * @return 返回登录所需的Token
     */
    @Override
    @Transactional
    public Token newMiniProgramAutoLogin(WechatMPLoginParams params) {
        // 从缓存中获取微信联合登陆信息
        Object cacheData = cache.get(CachePrefix.WECHAT_SESSION_PARAMS.getPrefix() + params.getUuid());
        Map<String, String> map = new HashMap<>(3);
        if (cacheData == null) {
            // 如果缓存中没有相应信息，调用 getConnect 方法获取微信联合登陆信息
            JSONObject json = this.getConnect(params.getCode(),params.getAppid());
            System.out.println("json:" + json);

            // 从返回的结果中获取 session_key、unionid 和 openid
            String sessionKey = json.getStr("session_key");
            String unionId = json.getStr("unionid");
            String openId = json.getStr("openid");
            System.out.println("sessionKey:"+sessionKey);
            System.out.println("unionId:"+unionId);
            System.out.println("openId:"+openId);

            // 将获取到的信息存入 map 中，并将其存储到缓存中
            map.put("sessionKey", sessionKey);
            map.put("unionId", unionId);
            map.put("openId", openId);
            System.out.println(map);
            // 缓存十五分钟（即十五分钟内免登录）
            cache.put(CachePrefix.WECHAT_SESSION_PARAMS.getPrefix() + params.getUuid(), map, 900L);
        } else {
            // 如果缓存中存在相应信息，直接将其转换成 map
            map = (Map<String, String>) cacheData;
            System.out.println(map);
        }

        return autoLogin(params, map.get("openId"), map.get("unionId"),params.getAppid());
    }

    /**
     * 通过微信返回的code 获取openid 等信息
     *
     * @param code 微信code
     * @param appid 小程序appid
     * @return 微信返回的信息
     */
    public JSONObject getConnect(String code,String appid) {
        WechatConnectSettingItem setting = getWechatMPSetting(appid);
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + setting.getAppId() + "&" +
                "secret=" + setting.getAppSecret() + "&" +
                "js_code=" + code + "&" +
                "grant_type=authorization_code";
        String content = HttpUtils.doGet(url, "UTF-8", 100, 1000);
        log.error(content);
        return JSONUtil.parseObj(content);
    }

    /**
     * 手机号 绑定 且 自动登录
     *
     * @param sessionKey 微信sessionKey
     * @param params     微信小程序自动登录参数
     * @param openId     微信openid
     * @param unionId    微信unionid
     * @param appid      小程序id
     * @return token
     */
    @Transactional(rollbackFor = Exception.class)
    public Token phoneMpBindAndLogin(String sessionKey, WechatMPLoginParams params, String openId, String unionId,String appid) {
        // 获取加密数据和向量，解密用户信息
        String encryptedData = params.getEncryptedData();
        String iv = params.getIv();
        JSONObject userInfo = this.getUserInfo(encryptedData, sessionKey, iv);

        // 日志记录联合登陆返回信息
        log.info("联合登陆返回：{}", userInfo.toString());

        // 根据appid获取租户信息
        Tenant tenant =  tenantAreaService.getTenantByAppId(appid);

        // 记录租户的访问次数
        serviceStatisticsService.addServiceCount(ServiceTypeEnum.WechatMPLogin.name(),tenant.getId());

        // 获取用户手机号
        String phone = (String) userInfo.get("purePhoneNumber");

        // 如果用户手机号为空，则默认设置为 1
        if(phone == null){
            phone = "1";
        }

        // 根据手机号查询会员是否存在
        LambdaQueryWrapper<Member> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Member::getMobile, phone);
        Member member = memberService.getOne(lambdaQueryWrapper);

        // 如果会员存在，则进行绑定微信 openid 和 unionid 操作，并生成 token 进行登录
        if (member != null) {
            // 将小程序与用户绑定
            bindMpMember(openId, unionId, member,appid);
            // 给租户添加用户、给用户绑定租户（如果已绑定则不做任何处理）
            memberTenantService.addTenantId(tenant.getId(),member.getId());
            // 返回登录令牌
            return memberTokenGenerate.createToken(member, true);
        }

        //如果没有会员，则根据手机号注册会员
        Member newMember = new Member("m" + phone, "111111", phone, params.getNickName(), params.getImage());
        memberService.save(newMember);

        // 获取为用户注册好的账号信息
        newMember = memberService.findByUsername(newMember.getUsername());

        // 给租户添加用户
        memberTenantService.addTenantId(tenant.getId(),newMember.getId());

        //判定有没有邀请人并且写入
        UserContext.settingInviter(newMember.getId(), cache);

        // 将小程序与用户绑定
        bindMpMember(openId, unionId, newMember,appid);

        // 发送会员注册信息
        applicationEventPublisher.publishEvent(new TransactionCommitSendMQEvent("new member register", rocketmqCustomProperties.getMemberTopic(), MemberTagsEnum.MEMBER_REGISTER.name(), newMember));
        return memberTokenGenerate.createToken(newMember, true);
    }

    /**
     * 自动登录（无需手机号）
     *
     * @param params     微信小程序自动登录参数
     * @param openId     微信openid
     * @param unionId    微信unionId
     * @param appid      小程序id
     * @return token
     */
    @Transactional(rollbackFor = Exception.class)
    public Token autoLogin(WechatMPLoginParams params, String openId, String unionId,String appid) {

        // 根据appid获取租户信息
        Tenant tenant =  tenantAreaService.getTenantByAppId(appid);

        // 记录租户的访问次数
        serviceStatisticsService.addServiceCount(ServiceTypeEnum.WechatMPLogin.name(),tenant.getId());

        // 根据openId或unionId、unionType、appId 查询会员是否存在
        LambdaQueryWrapper<Connect> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (unionId != null){
            lambdaQueryWrapper.eq(Connect::getUnionId, unionId);
        }else {
            lambdaQueryWrapper.eq(Connect::getUnionId, openId);
        }
        lambdaQueryWrapper.eq(Connect::getUnionType, ConnectEnum.WECHAT_MP_OPEN_ID.name());
        lambdaQueryWrapper.eq(Connect::getAppid,appid);

        // 查询符合条件的 Connect 对象
        Connect connect = this.getOne(lambdaQueryWrapper);

        // 如果会员存在，则进行绑定微信 openid 和 unionId 操作，并生成 token 进行登录
        if (connect != null) {
            Member member = memberService.getById(connect.getUserId());
            // 将小程序与用户绑定
            bindMpMember(openId, unionId, member,appid);
            // 给租户添加用户、给用户绑定租户（如果已绑定则不做任何处理）
            memberTenantService.addTenantId(tenant.getId(),member.getId());
            // 返回登录令牌
            return memberTokenGenerate.createToken(member, true);
        }

        //如果没有会员，则根据openId或unionId注册会员
        String username = openId != null ? openId : unionId;
        String nickName = params.getNickName() != null ? params.getNickName() : "临时昵称";
        String face = params.getImage() != null ? params.getImage() : "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
        Member newMember = new Member(username, "111111", null, nickName, face);
        memberService.save(newMember);

        // 获取为用户注册好的账号信息
        newMember = memberService.findByUsername(newMember.getUsername());

        // 给租户添加用户
        memberTenantService.addTenantId(tenant.getId(),newMember.getId());

        //判定有没有邀请人并且写入
        UserContext.settingInviter(newMember.getId(), cache);

        // 将小程序与用户绑定
        bindMpMember(openId, unionId, newMember,appid);

        // 发送会员注册信息
        applicationEventPublisher.publishEvent(new TransactionCommitSendMQEvent("new member register", rocketmqCustomProperties.getMemberTopic(), MemberTagsEnum.MEMBER_REGISTER.name(), newMember));
        return memberTokenGenerate.createToken(newMember, true);
    }

    @Override
    public Connect queryConnect(ConnectQueryDTO connectQueryDTO) {

        LambdaQueryWrapper<Connect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CharSequenceUtil.isNotEmpty(connectQueryDTO.getUserId()), Connect::getUserId, connectQueryDTO.getUserId())
                .eq(CharSequenceUtil.isNotEmpty(connectQueryDTO.getUnionType()), Connect::getUnionType, connectQueryDTO.getUnionType())
                .eq(CharSequenceUtil.isNotEmpty(connectQueryDTO.getUnionId()), Connect::getUnionId, connectQueryDTO.getUnionId())
                .eq(CharSequenceUtil.isNotEmpty(connectQueryDTO.getAppid()), Connect::getAppid, connectQueryDTO.getAppid());

        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteByMemberId(String userId) {
        LambdaQueryWrapper<Connect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Connect::getUserId, userId);
        this.remove(queryWrapper);
    }

    /**
     * 会员绑定 绑定微信小程序
     * <p>
     * 如果openid 已经绑定其他账号，则这里不作处理，如果未绑定，则绑定最新的会员
     * 这样，微信小程序注册之后，其他app 公众号页面，都可以实现绑定自动登录功能
     * </p>
     *
     * @param openId  微信openid
     * @param unionId 微信unionid
     * @param member  会员
     */
    private void bindMpMember(String openId, String unionId, Member member,String appid) {

        //如果unionId 不为空  则为账号绑定unionId
        if (CharSequenceUtil.isNotEmpty(unionId)) {
            LambdaQueryWrapper<Connect> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Connect::getUnionId, unionId);
            lambdaQueryWrapper.eq(Connect::getUnionType, ConnectEnum.WECHAT.name());
            lambdaQueryWrapper.eq(Connect::getAppid,appid);

            // 查询符合条件的 Connect 对象列表
            List<Connect> connects = this.list(lambdaQueryWrapper);

            // 如果列表为空，表示不存在对应的绑定关系，需要新建并保存
            if (connects.isEmpty()) {
                Connect connect = new Connect();
                connect.setUnionId(unionId);
                connect.setUserId(member.getId());
                connect.setAppid(appid);
                connect.setUnionType(ConnectEnum.WECHAT.name());
                this.save(connect);
            }
        }

        //如果openId 不为空  则为账号绑定openId
        if (CharSequenceUtil.isNotEmpty(openId)) {
            LambdaQueryWrapper<Connect> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Connect::getUnionId, openId);
            lambdaQueryWrapper.eq(Connect::getUnionType, ConnectEnum.WECHAT_MP_OPEN_ID.name());
            lambdaQueryWrapper.eq(Connect::getAppid,appid);

            List<Connect> connects = this.list(lambdaQueryWrapper);

            if (connects.isEmpty()) {
              Connect connect = new Connect();
              connect.setUnionId(openId);
              connect.setUserId(member.getId());
              connect.setAppid(appid);
              connect.setUnionType(ConnectEnum.WECHAT_MP_OPEN_ID.name());
              this.save(connect);
            }
        }
    }


    /**
     * 获取微信小程序配置
     *
     * @return 微信小程序配置
     */
    private WechatConnectSettingItem getWechatMPSetting(String appid) {
        Tenant tenant =  tenantAreaService.getTenantByAppId(appid);
        Setting setting = settingService.getByIdAndTenantId(SettingEnum.WECHAT_CONNECT.name(),tenant.getId());

        WechatConnectSetting wechatConnectSetting = JSONUtil.toBean(setting.getSettingValue(), WechatConnectSetting.class);

        if (wechatConnectSetting == null) {
            throw new ServiceException(ResultCode.WECHAT_CONNECT_NOT_EXIST);
        }
        //寻找对应对微信小程序登录配置
        for (WechatConnectSettingItem wechatConnectSettingItem : wechatConnectSetting.getWechatConnectSettingItems()) {
            if (wechatConnectSettingItem.getClientType().equals(ClientTypeEnum.WECHAT_MP.name())) {
                return wechatConnectSettingItem;
            }
        }

        throw new ServiceException(ResultCode.WECHAT_CONNECT_NOT_EXIST);
    }


    /**
     * 解密，获取微信信息
     *
     * @param encryptedData 加密信息
     * @param sessionKey    微信sessionKey
     * @param iv            微信揭秘参数
     * @return 用户信息
     */
    public JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {

        log.info("encryptedData:{},sessionKey:{},iv:{}", encryptedData, sessionKey, iv);
        //被加密的数据
        byte[] dataByte = Base64.getDecoder().decode(encryptedData);
        //加密秘钥
        byte[] keyByte = Base64.getDecoder().decode(sessionKey);
        //偏移量
        byte[] ivByte = Base64.getDecoder().decode(iv);
        try {
            //如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            //初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            //初始化
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                return JSONUtil.parseObj(result);
            }
        } catch (Exception e) {
            log.error("解密，获取微信信息错误", e);
        }
        throw new ServiceException(ResultCode.USER_CONNECT_ERROR);
    }
}
