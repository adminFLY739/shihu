package cn.lili.modules.promotion.mapper;

import cn.lili.modules.order.cart.entity.vo.FullDiscountVO;
import cn.lili.modules.order.order.entity.vo.PaymentLog;
import cn.lili.modules.promotion.entity.dos.FullDiscount;
import cn.lili.modules.promotion.entity.dos.Seckill;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 满优惠数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface FullDiscountMapper extends BaseMapper<FullDiscount> {

    /**
     * 查询满额
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select me.* , tenant.name from li_store_tenant as st join li_store as s on st.store_id = s.id join li_full_discount as me on me.store_id = s.id join tenant on st.tenant_id = tenant.id ${ew.customSqlSegment} ")
    IPage<FullDiscountVO> queryFullDiscount(IPage<FullDiscountVO> page, @Param(Constants.WRAPPER) Wrapper<FullDiscountVO> queryWrapper);


    /**
     * 店铺查询满额活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_full_discount as me ${ew.customSqlSegment} ")
    IPage<FullDiscount> storeQueryFullDiscount(IPage<FullDiscount> page, @Param(Constants.WRAPPER) Wrapper<FullDiscount> queryWrapper);

}
