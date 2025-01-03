package cn.lili.modules.logistics;

import cn.hutool.json.JSONUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.logistics.entity.enums.LogisticsEnum;
import cn.lili.modules.logistics.plugin.kdniao.KdniaoPlugin;
import cn.lili.modules.logistics.plugin.kuaidi100.Kuaidi100Plugin;
import cn.lili.modules.statistics.service.ServiceStatisticsService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.LogisticsSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物流接口抽象工厂 直接返回操作类
 *
 * @author Bulbasaur
 * @version v1.0
 * 2022-06-06 11:35
 */

@Component
public class LogisticsPluginFactory {

    /**
     * 设置
     */
    @Autowired
    private SettingService settingService;

    /**
     * 业务统计
     */
    @Autowired
    private ServiceStatisticsService serviceStatisticsService;


    /**
     * 获取logistics client
     */
    public LogisticsPlugin filePlugin(String tenantId) {

        LogisticsSetting logisticsSetting = null;
        try {
            Setting setting = settingService.getByIdAndTenantId(SettingEnum.LOGISTICS_SETTING.name(),tenantId);
            logisticsSetting = JSONUtil.toBean(setting.getSettingValue(), LogisticsSetting.class);
            switch (LogisticsEnum.valueOf(logisticsSetting.getType())) {
                case KDNIAO:
                    return new KdniaoPlugin(logisticsSetting,serviceStatisticsService);
                case KUAIDI100:
                    return new Kuaidi100Plugin(logisticsSetting);
                default:
                    throw new ServiceException();
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
    }


}
