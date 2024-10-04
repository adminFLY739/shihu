package cn.lili.modules.order.order.mapper;

import cn.lili.modules.order.order.entity.dos.OrderComplaint;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单投诉数据处理层
 *
 * @author paulG
 * @since 2020/12/5
 **/
public interface OrderComplaintMapper extends BaseMapper<OrderComplaint> {

    /**
     * 获取订单投诉分页
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 售后VO分页
     */
    @Select("SELECT * FROM li_order_complaint as me join li_goods as m on me.goods_id = m.id ${ew.customSqlSegment}")
    IPage<OrderComplaint> queryByParams(IPage<OrderComplaint> page, @Param(Constants.WRAPPER) Wrapper<OrderComplaint> queryWrapper);
}
