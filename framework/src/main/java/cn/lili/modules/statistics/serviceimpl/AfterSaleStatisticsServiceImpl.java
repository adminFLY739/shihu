package cn.lili.modules.statistics.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.order.aftersale.entity.dos.AfterSale;
import cn.lili.modules.order.aftersale.entity.vo.AfterSaleSearchParams;
import cn.lili.modules.order.aftersale.service.AfterSaleService;
import cn.lili.modules.order.trade.entity.enums.AfterSaleStatusEnum;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import cn.lili.modules.statistics.mapper.AfterSaleStatisticsMapper;
import cn.lili.modules.statistics.service.AfterSaleStatisticsService;
import cn.lili.modules.statistics.util.StatisticsDateUtil;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Objects;

/**
 * 售后统计业务层实现
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:30
 */
@Service
public class AfterSaleStatisticsServiceImpl extends ServiceImpl<AfterSaleStatisticsMapper, AfterSale> implements AfterSaleStatisticsService {

    @Autowired
    private AfterSaleService afterSaleService;


    @Override
    public long applyNum(String serviceType , String tenantId) {
        AuthUser authUser = Objects.requireNonNull(UserContext.getCurrentUser());

        AfterSaleSearchParams afterSaleSearchParams = new AfterSaleSearchParams();
        afterSaleSearchParams.setServiceStatus(AfterSaleStatusEnum.APPLY.name());
        if(CharSequenceUtil.isNotEmpty(serviceType)){
            afterSaleSearchParams.setServiceStatus(serviceType);
        }
        if(CharSequenceUtil.equals(authUser.getRole().name(), UserEnums.STORE.name())){
            afterSaleSearchParams.setStoreId(authUser.getStoreId());
        }
        if(CharSequenceUtil.isNotEmpty(tenantId)){
            afterSaleSearchParams.setTenantId(tenantId);
        }
        return afterSaleService.getAfterSalePages(afterSaleSearchParams).getTotal();
    }


    @Override
    public IPage<AfterSale> getStatistics(StatisticsQueryParam statisticsQueryParam, PageVO pageVO) {

        QueryWrapper<AfterSale> queryWrapper = new QueryWrapper<>();
        Date[] dates = StatisticsDateUtil.getDateArray(statisticsQueryParam);
        queryWrapper.between("me.create_time", dates[0], dates[1]);
        queryWrapper.eq(CharSequenceUtil.isNotEmpty(statisticsQueryParam.getStoreId()), "me.store_id", statisticsQueryParam.getStoreId());
        queryWrapper.eq(CharSequenceUtil.isNotEmpty(statisticsQueryParam.getTenantId()), "tenant_id", statisticsQueryParam.getTenantId());
        return baseMapper.getAfterSaleStatistics(PageUtil.initPage(pageVO), queryWrapper);
    }

}
