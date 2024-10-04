package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.Coupon;
import cn.lili.modules.promotion.entity.dos.Seckill;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 优惠券数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 查询优惠券
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 优惠券分页
     */
    @Select("select * from li_coupon as me ${ew.customSqlSegment} ")
    IPage<Coupon> queryCoupon(IPage<Coupon> page, @Param(Constants.WRAPPER) Wrapper<Coupon> queryWrapper);


}
