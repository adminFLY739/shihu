package cn.lili.modules.payment.service;

import cn.lili.modules.payment.entity.RefundLog;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 退款日志 业务层
 *
 * @author Chopper
 * @since 2020-12-19 09:25
 */
public interface RefundLogService extends IService<RefundLog> {
    /**
     * 根据售后sn查询退款日志
     * @param sn  订单sn
     * @return 退款日志
     */
    RefundLog queryByAfterSaleSn(String sn);

    /**
     * 查询订单退款记录
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    IPage<RefundLog> queryRefundLogs(IPage<RefundLog> page, Wrapper<RefundLog> queryWrapper);


}
