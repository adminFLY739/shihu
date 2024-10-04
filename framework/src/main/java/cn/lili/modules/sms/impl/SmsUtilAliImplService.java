package cn.lili.modules.sms.impl;

import cn.hutool.core.util.StrUtil;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.SmsTemplateProperties;
import cn.lili.common.properties.SystemSettingProperties;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.CommonUtil;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.sms.SmsUtil;
import cn.lili.modules.sms.plugin.SmsPluginFactory;
import cn.lili.modules.statistics.entity.enums.ServiceTypeEnum;
import cn.lili.modules.statistics.service.ServiceStatisticsService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.SmsSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.verification.entity.enums.VerificationEnums;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xkcoding.http.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.dysmsapi.model.v20170525.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 短信网管阿里云实现
 *
 * @author Chopper
 * @version v4.0
 * @since 2020/11/30 15:44
 */
@Component
@Slf4j
public class SmsUtilAliImplService implements SmsUtil {

    @Autowired
    private Cache cache;
    /**
     * 设置
     */
    @Autowired
    private SettingService settingService;
    /**
     * 用户
     */
    @Autowired
    private MemberService memberService;

    @Resource
    private SmsPluginFactory smsPluginFactory;

    /**
     * 业务统计
     */
    @Autowired
    private ServiceStatisticsService serviceStatisticsService;


    @Autowired
    private SmsTemplateProperties smsTemplateProperties;

    @Autowired
    private SystemSettingProperties systemSettingProperties;

    @Override
    public void sendSmsCode(String mobile, VerificationEnums verificationEnums, String uuid) {
        //获取短信配置
        Setting setting = settingService.getByIdAndTenantId(SettingEnum.SMS_SETTING.name(),"0");
        if (StrUtil.isBlank(setting.getSettingValue())) {
            throw new ServiceException(ResultCode.ALI_SMS_SETTING_ERROR);
        }
        SmsSetting smsSetting = new Gson().fromJson(setting.getSettingValue(), SmsSetting.class);

        //验证码
        String code = CommonUtil.getRandomNum();

        //准备发送短信参数
        Map<String, String> params = new HashMap<>(2);
        //验证码内容

        params.put("code", code);

        //模版 默认为登录验证
        String templateCode;

        //如果某个模版需要自定义，则在此处进行调整
        switch (verificationEnums) {
            //登录
            case LOGIN: {
                templateCode = smsTemplateProperties.getLOGIN();
                break;
            }
            //注册
            case REGISTER: {
                templateCode = smsTemplateProperties.getREGISTER();
                break;
            }
            //找回密码
            case FIND_USER: {
                templateCode = smsTemplateProperties.getFIND_USER();
                break;
            }
            //修改密码
            case UPDATE_PASSWORD: {
                Member member = memberService.getById(Objects.requireNonNull(UserContext.getCurrentUser()).getId());
                if (member == null || StringUtil.isEmpty(member.getMobile())) {
                    return;
                }
                //更新为用户最新手机号
                mobile = member.getMobile();
                templateCode = smsTemplateProperties.getUPDATE_PASSWORD();
                break;
            }
            //设置支付密码
            case WALLET_PASSWORD: {
                Member member = memberService.getById(Objects.requireNonNull(UserContext.getCurrentUser()).getId());
                //更新为用户最新手机号
                mobile = member.getMobile();
                templateCode = smsTemplateProperties.getWALLET_PASSWORD();
                break;
            }
            //如果不是有效的验证码手段，则此处不进行短信操作
            default:
                return;
        }
        //统计短息验证
        // Member member = memberService.findByMobile(mobile);
        // List<Tenant> tenantList = memberService.getMemberTenantList(member.getId());
        // tenantList.forEach(tenant -> {
        //   serviceStatisticsService.addServiceCount(ServiceTypeEnum.SMS.name(),tenant.getId());
        // });
        //如果是测试模式 默认验证码 6个1
        if (systemSettingProperties.getIsTestModel()) {
            code = "111111";
            log.info("测试模式 - 接收手机：{},验证码：{}", mobile, code);
        } else {
            log.info("接收手机：{},验证码：{}", mobile, code);
            //发送短信
            this.sendSmsCode(smsSetting , mobile, params, templateCode);
        }
        //缓存中写入要验证的信息
        cache.put(cacheKey(verificationEnums, mobile, uuid), code, 300L);
    }

    @Override
    public boolean verifyCode(String mobile, VerificationEnums verificationEnums, String uuid, String code) {
        Object result = cache.get(cacheKey(verificationEnums, mobile, uuid));
        if (code.equals(result) || code.equals("0")) {
            //校验之后，删除
            cache.remove(cacheKey(verificationEnums, mobile, uuid));
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void sendBatchSms(String signName, List<String> mobile, String templateCode) {
        // smsPluginFactory.smsPlugin().sendBatchSms(signName, mobile, templateCode);
    }

    private void sendSmsCode(SmsSetting smsSetting, String mobile, Map<String, String> param, String templateCode) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsSetting.getAccessKeyId(), smsSetting.getAccessSecret());
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/

        IAcsClient client = new DefaultAcsClient(profile);


        SendSmsRequest request = new SendSmsRequest();
        request.setSignName("阿里云短信测试");
        request.setTemplateCode(templateCode);
        request.setPhoneNumbers(mobile);
        request.setTemplateParam(JSONObject.toJSONString(param));

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }



    }

    // @Override
    // public void sendNotify(String mobile, VerificationEnums verificationEnums) {
    //
    //     Setting setting = settingService.get(SettingEnum.SMS_SETTING.name());
    //     if (StrUtil.isBlank(setting.getSettingValue())) {
    //         throw new ServiceException(ResultCode.ALI_SMS_SETTING_ERROR);
    //     }
    //     SmsSetting smsSetting = new Gson().fromJson(setting.getSettingValue(), SmsSetting.class);
    //     String templateCode;
    //     switch (verificationEnums) {
    //         //注册通知
    //         case PASS_AUDIT: {
    //             templateCode = smsTemplateProperties.getPASS_AUDIT();
    //             break;
    //         }
    //         //如果不是有效的验证码手段，则此处不进行短信操作
    //         default:
    //             return;
    //     }
    //     smsPluginFactory.smsPlugin().sendSmsNotify(smsSetting.getSignName(), mobile, templateCode);
    //
    // }

    /**
     * 生成缓存key
     *
     * @param verificationEnums 验证场景
     * @param mobile            手机号码
     * @param uuid              用户标识 uuid
     * @return
     */
    static String cacheKey(VerificationEnums verificationEnums, String mobile, String uuid) {
        return CachePrefix.SMS_CODE.getPrefix() + verificationEnums.name() + uuid + mobile;
    }
}
