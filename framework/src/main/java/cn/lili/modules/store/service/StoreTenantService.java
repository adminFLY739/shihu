package cn.lili.modules.store.service;

import cn.lili.modules.store.entity.dos.StoreTenant;
import cn.lili.modules.tenant.entity.dos.Tenant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/15 13:23
 * @description: 店铺租户业务接口层
 */
public interface StoreTenantService extends IService<StoreTenant> {


    /**
    *
    * 修改某个租户下店铺状态
    *
    *@Param: storeId  店铺id
    *@Param: tenantId 租户id
    *@Param: state    店铺状态
    *@return: 修改结果
    *@Author: nxc
    *@date: 2023/6/15
    */
    boolean ChangeStoreDisable(String storeId , String tenantId ,String status);

    /**
    *
    * 获取当前店铺所属租户
    *
    *@param storeId 店铺id
    *@return: 租户列表
    *@Author: nxc
    *@date: 2023/6/15
    */
    List<Tenant> getTenantListByStoreId(String storeId);



    /**
     *
     * 获取当前店铺所属租户
     *
     *@param storeId 店铺id
     *@param status  店铺状态
     *@return: 租户列表
     *@Author: nxc
     *@date: 2023/6/15
     */
    List<StoreTenant> getStoreTenantByStatus(String storeId ,String status);

    /**
     *
     * 获取当前店铺所属租户
     *
     *@param storeId 店铺id
     *@param tenantId  租户id
     *@return: 租户列表
     *@Author: nxc
     *@date: 2023/6/15
     */
    StoreTenant getStoreTenant(String storeId ,String tenantId);
}
