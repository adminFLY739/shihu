package cn.lili.modules.promotion.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.promotion.entity.dos.Coupon;
import cn.lili.modules.promotion.entity.dos.Seckill;
import cn.lili.modules.promotion.entity.dto.search.CouponSearchParams;
import cn.lili.modules.promotion.entity.vos.CouponVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 优惠券业务层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface CouponService extends AbstractPromotionsService<Coupon> {


    /**
     * 查询优惠券
     *
     * @param page         分页
     * @param queryParam 查询条件
     * @return 订单支付记录分页
     */
    IPage<CouponVO> queryCoupon(CouponSearchParams queryParam, PageVO page);

    /**
     * 领取优惠券
     *
     * @param couponId   优惠券id
     * @param receiveNum 领取数量
     */
    void receiveCoupon(String couponId, Integer receiveNum);

    /**
     * 使用优惠券
     *
     * @param couponId 优惠券id
     * @param usedNum  使用数量
     */
    void usedCoupon(String couponId, Integer usedNum);

    /**
     * 获取优惠券展示实体
     *
     * @param searchParams 查询参数
     * @param page 分页参数
     * @return 优惠券展示实体列表
     */
    IPage<CouponVO> pageVOFindAll(CouponSearchParams searchParams, PageVO page);

    /**
     * 获取优惠券展示详情
     *
     * @param couponId 优惠券id
     * @return 返回优惠券展示详情
     */
    CouponVO getDetail(String couponId);

}
