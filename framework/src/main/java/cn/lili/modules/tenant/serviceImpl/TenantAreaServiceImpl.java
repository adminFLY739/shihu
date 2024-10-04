package cn.lili.modules.tenant.serviceImpl;

import cn.lili.modules.page.entity.dos.Article;
import cn.lili.modules.page.entity.dos.PageData;
import cn.lili.modules.page.service.ArticleService;
import cn.lili.modules.page.service.PageDataService;
import cn.lili.modules.system.entity.dos.Logistics;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.service.LogisticsService;
import cn.lili.modules.system.service.SettingService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.mapper.TenantAreaMapper;
import cn.lili.modules.tenant.service.TenantAreaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/12 09:56
 * @description: 租户区域业务实现层
 */

@Service
public class TenantAreaServiceImpl extends ServiceImpl<TenantAreaMapper, Tenant> implements TenantAreaService {

    /**
     * 楼层装修
     */
    @Autowired
    private PageDataService pageDataService;

    /**
     * 设置
     */
    @Autowired
    private SettingService settingService;

    /**
     * 文章
     */
    @Autowired
    private ArticleService articleService;

    /**
     * 物流公司
     */
    @Autowired
    private LogisticsService logisticsService;

    @Override
    public void saveTenant(Tenant tenant) {
        this.save(tenant);
        addOther(tenant.getId());
    }

  @Override
  public Tenant getTenantByAppId(String appid) {
    QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("appid",appid);
    return this.getOne(queryWrapper);
  }

  private void addOther(String tenantId){
      //装修界面
      PageData pageData = new PageData(tenantId);
      pageDataService.save(pageData);
      //ALIPAY_PAYMENT
//      Setting aliPaySetting = settingService.getByIdAndTenantId("ALIPAY_PAYMENT","0");
//      aliPaySetting.setTenantId(tenantId);
//      settingService.save(aliPaySetting);
      //BASE_SETTING
//      Setting baseSetting = settingService.getByIdAndTenantId("BASE_SETTING","0");
//      baseSetting.setTenantId(tenantId);
//      settingService.save(baseSetting);
      //DISTRIBUTION_SETTING
//      Setting distributtonSetting = settingService.getByIdAndTenantId("DISTRIBUTION_SETTING","0");
//      distributtonSetting.setTenantId(tenantId);
//      settingService.save(distributtonSetting);
      //GOODS_SETTING
      Setting goodsSetting = settingService.getByIdAndTenantId("GOODS_SETTING","0");
      goodsSetting.setTenantId(tenantId);
      settingService.save(goodsSetting);
      //IM_SETTING
      Setting imSetting = settingService.getByIdAndTenantId("IM_SETTING","0");
      imSetting.setTenantId(tenantId);
      settingService.save(imSetting);
      //LOGISTICS_SETTING
      Setting logisticsSetting = settingService.getByIdAndTenantId("LOGISTICS_SETTING","0");
       logisticsSetting.setTenantId(tenantId);
      settingService.save(logisticsSetting);
      //KUAIDI_SETTING
//      Setting kuaidiSetting = settingService.getByIdAndTenantId("KUAIDI_SETTING","0");
//      kuaidiSetting.setTenantId(tenantId);
//      settingService.save( kuaidiSetting);
      //ORDER_SETTING
//      Setting orderSetting = settingService.getByIdAndTenantId("ORDER_SETTING","0");
//      orderSetting.setTenantId(tenantId);
//      settingService.save(orderSetting);
      //OSS_SETTING
//      Setting ossSetting = settingService.getByIdAndTenantId("OSS_SETTING","0");
//      ossSetting.setTenantId(tenantId);
//      settingService.save(ossSetting);
      //PAYMENT_SUPPORT
//      Setting paymentSetting = settingService.getByIdAndTenantId("PAYMENT_SUPPORT","0");
//      paymentSetting.setTenantId(tenantId);
//      settingService.save(paymentSetting);
      //POINT_SETTING
//      Setting pointSetting = settingService.getByIdAndTenantId("POINT_SETTING","0");
//      pointSetting.setTenantId(tenantId);
//      settingService.save(pointSetting);
      //QQ_CONNECT
//      Setting qqConectSetting = settingService.getByIdAndTenantId("QQ_CONNECT","0");
//      qqConectSetting.setTenantId(tenantId);
//      settingService.save(qqConectSetting);
      //SMS_SETTING
//      Setting smsSetting = settingService.getByIdAndTenantId("SMS_SETTING","0");
//      smsSetting.setTenantId(tenantId);
//      settingService.save(smsSetting);
      //WECHAT_CONNECT
      Setting wechatSetting = settingService.getByIdAndTenantId("WECHAT_CONNECT","0");
      wechatSetting.setTenantId(tenantId);
      settingService.save(wechatSetting);
      //WECHAT_PAYMENT
      Setting wechatPaymentSetting = settingService.getByIdAndTenantId("WECHAT_PAYMENT","0");
      wechatPaymentSetting.setTenantId(tenantId);
      settingService.save(wechatPaymentSetting);
      //WITHDRAWAL_SETTING
//      Setting withdrawalSetting = settingService.getByIdAndTenantId("WITHDRAWAL_SETTING","0");
//      withdrawalSetting.setTenantId(tenantId);
//      settingService.save(withdrawalSetting);
    //文章
    List<Article> articleList = articleService.getByTenantId("0");
      articleList.forEach(article -> {
        article.setId(null);
        article.setTenantId(tenantId);
        articleService.save(article);
    });
      //物流公司
      List<Logistics> logisticsList = logisticsService.getOpenLogistics("0");
      logisticsList.forEach(logistics -> {
          logistics.setId(null);
          logistics.setTenantId(tenantId);
          logisticsService.save(logistics);
      });
    }
}
