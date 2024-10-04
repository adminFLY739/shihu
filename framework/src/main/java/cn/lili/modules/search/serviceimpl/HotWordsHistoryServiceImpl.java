package cn.lili.modules.search.serviceimpl;

import cn.lili.common.utils.DateUtil;
import cn.lili.modules.search.entity.dos.HotWordsHistory;
import cn.lili.modules.search.entity.dto.HotWordsSearchParams;
import cn.lili.modules.search.mapper.HotWordsHistoryMapper;
import cn.lili.modules.search.service.HotWordsHistoryService;
import cn.lili.modules.search.service.HotWordsService;
import cn.lili.modules.statistics.entity.enums.SearchTypeEnum;
import cn.lili.modules.statistics.util.StatisticsDateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 历史热词
 *
 * @author paulG
 * @since 2020/10/15
 **/
@Service
public class HotWordsHistoryServiceImpl extends ServiceImpl<HotWordsHistoryMapper, HotWordsHistory> implements HotWordsHistoryService {

    @Autowired
    private HotWordsService hotWordsService;

    @Override
    public List<HotWordsHistory> statistics(HotWordsSearchParams hotWordsSearchParams) {
        if (hotWordsSearchParams.getSearchType().equals(SearchTypeEnum.TODAY.name())) {
            return hotWordsService.getHotWordsVO(hotWordsSearchParams.getTop(),hotWordsSearchParams.getTenantId());
        }
        QueryWrapper<HotWordsHistory> queryWrapper = hotWordsSearchParams.queryWrapper();

        queryWrapper.groupBy("keywords");
        queryWrapper.orderByDesc("score");
        queryWrapper.last("limit " + hotWordsSearchParams.getTop());
        return baseMapper.statistics(queryWrapper);
    }

    @Override
    public List<HotWordsHistory> queryByDay(Date queryTime,String tenantId) {
        QueryWrapper<HotWordsHistory> queryWrapper = new QueryWrapper<>();

        Date[] dates = StatisticsDateUtil.getDateArray(queryTime);
        queryWrapper.between("create_time", dates[0], dates[1]);
        queryWrapper.eq("tenant_id", tenantId);
        return list(queryWrapper);
    }

}
