package cn.lili.modules.store.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.modules.store.entity.dos.StoreTenant;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.modules.store.mapper.StoreTenantMapper;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: nxc
 * @since: 2023/6/15 13:24
 * @description: 店铺租户业务实现层
 */

@Service
public class StoreTenantServiceImpl extends ServiceImpl<StoreTenantMapper, StoreTenant> implements StoreTenantService {

    @Autowired
    private TenantAreaService tenantAreaService;


    @Override
    public boolean ChangeStoreDisable(String storeId, String tenantId,String status) {

        UpdateWrapper<StoreTenant> updateWrapper = Wrappers.update();
        updateWrapper.eq("store_id", storeId);
        updateWrapper.eq("tenant_id", tenantId);
        updateWrapper.set("store_disable", status);
        return this.update(updateWrapper);
    }

    @Override
    public List<Tenant> getTenantListByStoreId(String storeId) {

        LambdaQueryWrapper<StoreTenant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreTenant::getStoreId, storeId);
        lambdaQueryWrapper.eq(StoreTenant::getStoreDisable, StoreStatusEnum.OPEN.name());
        List<StoreTenant> storeTenants = this.list(lambdaQueryWrapper);

        List<String> tenantList = storeTenants.stream().map(StoreTenant::getTenantId).collect(Collectors.toList());

        List<Tenant> tenantAllList = tenantAreaService.list();

        return tenantAllList.stream().filter(tenant -> tenantList.contains(tenant.getId())).collect(Collectors.toList());
    }

    @Override
    public List<StoreTenant> getStoreTenantByStatus(String storeId ,String status){
        LambdaQueryWrapper<StoreTenant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(CharSequenceUtil.isNotEmpty(storeId)){
            lambdaQueryWrapper.eq(StoreTenant::getStoreId, storeId);
        }
        if(CharSequenceUtil.isNotEmpty(status)){
            lambdaQueryWrapper.eq(StoreTenant::getStoreDisable, status);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public StoreTenant getStoreTenant(String storeId, String tenantId) {
        QueryWrapper<StoreTenant> queryWrapper = new QueryWrapper<>();
        if(CharSequenceUtil.isNotEmpty(storeId)){
            queryWrapper.eq("store_id", storeId);
        }
        if(CharSequenceUtil.isNotEmpty(tenantId)){
            queryWrapper.eq("tenant_id", tenantId);
        }
        return this.getOne(queryWrapper);
    }
}
