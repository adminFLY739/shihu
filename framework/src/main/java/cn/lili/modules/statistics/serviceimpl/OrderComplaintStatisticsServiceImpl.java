package cn.lili.modules.statistics.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.order.aftersale.entity.enums.ComplaintStatusEnum;
import cn.lili.modules.order.order.entity.dos.OrderComplaint;
import cn.lili.modules.order.order.entity.vo.OrderComplaintSearchParams;
import cn.lili.modules.order.order.service.OrderComplaintService;
import cn.lili.modules.statistics.mapper.OrderComplaintStatisticsMapper;
import cn.lili.modules.statistics.service.OrderComplaintStatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 交易投诉业务层实现
 *
 * @author paulG
 * @since 2020/12/5
 **/
@Service
public class OrderComplaintStatisticsServiceImpl extends ServiceImpl<OrderComplaintStatisticsMapper, OrderComplaint> implements OrderComplaintStatisticsService {


    @Autowired
    private OrderComplaintService orderComplaintService;

    @Override
    public long waitComplainNum(String tenantId) {

        OrderComplaintSearchParams orderComplaintSearchParams = new OrderComplaintSearchParams();
        if(StringUtils.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name())){
            orderComplaintSearchParams.setStoreId(UserContext.getCurrentUser().getStoreId());
        }
        if(CharSequenceUtil.isNotEmpty(tenantId)){
            orderComplaintSearchParams.setTenantId(tenantId);
        }
        QueryWrapper<OrderComplaint> queryWrapper = orderComplaintSearchParams.queryWrapper();
        queryWrapper.ne("complain_status", ComplaintStatusEnum.COMPLETE.name());

        return orderComplaintService.getOrderComplainByPage(new Page<OrderComplaint>(), queryWrapper).getTotal();
    }


}
