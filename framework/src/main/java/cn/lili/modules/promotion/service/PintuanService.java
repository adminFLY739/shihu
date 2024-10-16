package cn.lili.modules.promotion.service;

import cn.lili.modules.promotion.entity.dos.Pintuan;
import cn.lili.modules.promotion.entity.vos.PintuanMemberVO;
import cn.lili.modules.promotion.entity.vos.PintuanShareVO;
import cn.lili.modules.promotion.entity.vos.PintuanVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 拼图活动业务层
 *
 * @author Chopper
 * @since 2020/11/18 9:45 上午
 */
public interface PintuanService extends AbstractPromotionsService<Pintuan> {


    /**
     * 查询拼团活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    IPage<PintuanVO> queryPintuan(IPage<PintuanVO> page, Wrapper<PintuanVO> queryWrapper);

    /**
     * 获取当前拼团的会员
     *
     * @param pintuanId 拼图id
     * @return 当前拼团的会员列表
     */
    List<PintuanMemberVO> getPintuanMember(String pintuanId);

    /**
     * 查询拼团活动详情
     *
     * @param id 拼团ID
     * @return 拼团活动详情
     */
    PintuanVO getPintuanVO(String id);

    /**
     * 获取拼团分享信息
     *
     * @param parentOrderSn 拼团团长订单sn
     * @param skuId         商品skuId
     * @return 拼团分享信息
     */
    PintuanShareVO getPintuanShareInfo(String parentOrderSn, String skuId);


    /**
     * 店铺查询拼团活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    IPage<Pintuan> storeQueryPintuan(IPage<Pintuan> page, Wrapper<Pintuan> queryWrapper);


}
