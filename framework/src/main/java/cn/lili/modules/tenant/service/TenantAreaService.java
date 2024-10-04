package cn.lili.modules.tenant.service;

import cn.lili.modules.tenant.entity.dos.Tenant;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: nxc
 * @since: 2023/6/12 09:55
 * @description: 区域业务接口层
 */
public interface TenantAreaService extends IService<Tenant> {


    void saveTenant(Tenant tenant);

    /**
    *
    *  根据小程序id获取租户
    *
    *@Param: appid 小程序appid
    *@return: 租户
    *@Author: nxc
    *@date: 2023/8/9
    */
    Tenant getTenantByAppId(String appid);

}
