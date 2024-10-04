package cn.lili.modules.statistics.service;

import cn.lili.modules.store.entity.dos.Store;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 店铺统计业务层
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:06
 */
public interface StoreStatisticsService extends IService<Store> {

    /**
     * 获取待审核店铺数量
     * @param tenantId 租户Id
     * @return 待审核店铺数量
     */
    long auditNum(String tenantId);

    /**
     * 获取所有店铺数量
     *
     * @param tenantId 租户id
     * @return 店铺总数
     */
    long storeNum(String tenantId);

    /**
     * 获取今天的店铺数量
     *
     * @param tenantId 租户id
     * @return 今天的店铺数量
     */
    long todayStoreNum(String tenantId);
}
