package cn.lili.modules.system.service;

import cn.lili.modules.system.entity.dos.Setting;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 配置业务层
 *
 * @author Chopper
 * @since 2020/11/17 3:46 下午
 */
@CacheConfig(cacheNames = "{setting}")
public interface SettingService extends IService<Setting> {

    /**
     * 通过key获取
     *
     * @param key
     * @return
     */
    @Cacheable(key = "#key") //表示该方法的返回结果是可以缓存的
    Setting get(String key);

    /**
     * 修改
     *
     * @param setting
     * @return
     */
    @CacheEvict(key = "#setting.id + #setting.tenantId")
    boolean saveUpdate(Setting setting);


    /**
    *
    * 根据租户和id获取设置信息
    *
    *@Param: name 设置id
    *@Param: tenantId 租户id
    *@return: 设置信息
    *@Author: nxc
    *@date: 2023/8/9
    */
    @Cacheable(key = "#name + #tenantId") //表示该方法的返回结果是可以缓存的
    Setting getByIdAndTenantId(String name,String tenantId);
}
