package cn.lili.modules.payment.mapper;

import cn.lili.modules.order.order.entity.vo.PaymentLog;
import cn.lili.modules.payment.entity.RefundLog;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 退款日志数据层
 * @author Chopper
 * @since 2020-12-19 09:25
 */
public interface RefundLogMapper extends BaseMapper<RefundLog> {

    /**
     * 查询退款日志
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_refund_log as me join li_goods as m on me.goods_id = m.id  ${ew.customSqlSegment} ")
    IPage<RefundLog> queryRefundLogs(IPage<RefundLog> page, @Param(Constants.WRAPPER) Wrapper<RefundLog> queryWrapper);

}
