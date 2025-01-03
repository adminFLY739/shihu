package cn.lili.modules.promotion.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.promotion.entity.dos.MemberCoupon;
import cn.lili.modules.promotion.entity.dto.search.MemberCouponSearchParams;
import cn.lili.modules.promotion.entity.vos.MemberCouponVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 会员优惠券业务层
 *
 * @author Chopper
 * @since 2020/11/18 9:45 上午
 */
public interface MemberCouponService extends IService<MemberCoupon> {

    /**
     * 检查该会员领取优惠券的可领取数量
     *
     * @param couponId 优惠券编号
     * @param memberId 会员
     * @param tenantId 租户id
     */
    void checkCouponLimit(String couponId, String memberId,String tenantId);

    /**
     * 领取优惠券
     *
     * @param couponId   优惠券编号
     * @param memberId   会员
     * @param memberName 会员名称
     * @param tenantId   租户id
     */
    void receiveBuyerCoupon(String couponId, String memberId, String memberName,String tenantId);

    /**
     * 领取优惠券
     *
     * @param couponId   优惠券编号
     * @param memberId   会员
     * @param memberName 会员名称
     * @param tenantId   租户id
     */
    void receiveCoupon(String couponId, String memberId, String memberName,String tenantId);

    /**
     * 获取会员优惠券列表
     *
     * @param param  查询参数
     * @param pageVo 分页参数
     * @return 会员优惠券列表
     */
    IPage<MemberCoupon> getMemberCoupons(MemberCouponSearchParams param, PageVO pageVo);

    /**
     * 获取会员优惠券列表
     *
     * @param param  查询参数
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getMemberCoupons(MemberCouponSearchParams param);

    /**
     * 获取当前用户的优惠券列表（优先读取缓存）
     *
     * @param memberId 会员id
     * @param tenantId 租户id
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getMemberCoupons(String memberId,String tenantId);

    /**
     * 获取会员优惠券列表
     *
     * @param param      查询参数
     * @param totalPrice 当前商品总价
     * @param pageVo     分页参数
     * @return 会员优惠券列表
     */
    IPage<MemberCoupon> getMemberCouponsByCanUse(MemberCouponSearchParams param, Double totalPrice, PageVO pageVo);

    /**
     * 获取当前会员当前商品可用的会员优惠券
     *
     * @param memberId   会员Id
     * @param couponIds  优惠券id列表
     * @param totalPrice 当前商品总价
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getCurrentGoodsCanUse(String memberId, List<String> couponIds, Double totalPrice);

    /**
     * 获取当前会员全品类优惠券
     *
     * @param memberId 会员Id
     * @param storeId  店铺id
     * @return 会员优惠券列表
     */
    List<MemberCoupon> getAllScopeMemberCoupon(String memberId, List<String> storeId);

    /**
     * 获取会员优惠券
     *
     * @param param  查询参数
     * @return 会员优惠券列表
     */
    MemberCoupon getMemberCoupon(MemberCouponSearchParams param);

    /**
     * 获取会员优惠券数量
     *
     * @param  tenantId 租户id
     * @return 会员优惠券数量
     */
    long getMemberCouponsNum(String tenantId);

    /**
     * 使用优惠券
     *
     * @param tenantId 用户id
     * @param ids      会员优惠券id
     */
    void used(String memberId, List<String> ids,String tenantId);

    /**
     * 作废当前会员优惠券
     *
     * @param memberId 用户id
     * @param id       id
     */
    void cancellation(String memberId, String id);

    /**
     * 作废无效的会员优惠券
     *
     * @param memberId  用户id
     * @param tenantId  租户id
     * @return 是否操作成功
     */
    boolean expireInvalidMemberCoupon(String memberId,String tenantId);

    /**
     * 关闭会员优惠券
     *
     * @param couponIds 优惠券id集合
     */
    void closeMemberCoupon(List<String> couponIds);

    /**
     * 恢复会员优惠券
     *
     * @param memberCouponIds 会员优惠券id列表
     * @return 是否恢复成功
     */
    boolean recoveryMemberCoupon(List<String> memberCouponIds);

    /**
     * 作废优惠券
     *
     * @param couponId 优惠券ID
     */
    void voidCoupon(String couponId);

    /**
     * 获取会员优惠券列表
     *
     * @param page 分页参数
     * @param param 查询参数
     * @return 会员优惠券列表
     */
    Page<MemberCouponVO> getMemberCouponsPage(Page<MemberCoupon> page, MemberCouponSearchParams param);

}
