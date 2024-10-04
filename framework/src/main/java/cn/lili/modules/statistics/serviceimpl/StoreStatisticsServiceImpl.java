package cn.lili.modules.statistics.serviceimpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.statistics.mapper.StoreStatisticsMapper;
import cn.lili.modules.statistics.service.StoreStatisticsService;
import cn.lili.modules.store.entity.dos.Store;
import cn.lili.modules.store.entity.dos.StoreTenant;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.modules.store.entity.vos.StoreSearchParams;
import cn.lili.modules.store.service.StoreService;
import cn.lili.modules.store.service.StoreTenantService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品统计业务层实现
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:30
 */
@Service
public class StoreStatisticsServiceImpl extends ServiceImpl<StoreStatisticsMapper, Store> implements StoreStatisticsService {

    @Autowired
    StoreService storeService;

    @Autowired
    StoreTenantService storeTenantService;


    @Override
    public long auditNum(String tenantId) {

        StoreSearchParams storeSearchParams = new StoreSearchParams();
        storeSearchParams.setStoreDisable(StoreStatusEnum.APPLYING.name());
        if(StringUtils.isNotEmpty(tenantId)){
            storeSearchParams.setTenantId(tenantId);
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageSize(100);
        storeService.findByConditionPage(storeSearchParams, pageVO);
        return storeService.findByConditionPage(storeSearchParams, pageVO).getTotal();
    }

    @Override
    public long storeNum(String tenantId) {
        StoreSearchParams storeSearchParams = new StoreSearchParams();
        storeSearchParams.setStoreDisable(StoreStatusEnum.OPEN.name());
        if(CharSequenceUtil.isNotEmpty(tenantId)){
            storeSearchParams.setTenantId(tenantId);
        }
        return storeService.findByConditionPage(storeSearchParams,new PageVO()).getTotal();
    }

    @Override
    public long todayStoreNum(String tenantId) {
        LambdaQueryWrapper<StoreTenant> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StoreTenant::getStoreDisable, StoreStatusEnum.OPEN.name());
        queryWrapper.ge(StoreTenant::getCreateTime, DateUtil.beginOfDay(new DateTime()));
        queryWrapper.eq(CharSequenceUtil.isNotEmpty(tenantId),StoreTenant::getTenantId,tenantId);
        return storeTenantService.count(queryWrapper);
    }

}
