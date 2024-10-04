package cn.lili.modules.promotion.service;

import cn.lili.modules.promotion.entity.dos.Pintuan;
import cn.lili.modules.promotion.entity.dos.PointsGoods;
import cn.lili.modules.promotion.entity.vos.PointsGoodsVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 积分商品业务层
 *
 * @author paulG
 * @since 2020/11/18 9:45 上午
 **/
public interface PointsGoodsService extends AbstractPromotionsService<PointsGoods> {


    /**
     * 查询积分商品
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    IPage<PointsGoodsVO> queryPointsGoods(IPage<PointsGoodsVO> page, Wrapper<PointsGoodsVO> queryWrapper);

    /**
     * 批量保存库存商品
     *
     * @param promotionsList 积分商品列表
     * @return 是否保存成功
     */
    boolean savePointsGoodsBatch(List<PointsGoods> promotionsList);

    /**
     * 根据ID获取积分详情
     *
     * @param id 积分商品id
     * @return 积分详情
     */
    PointsGoodsVO getPointsGoodsDetail(String id);

    /**
     * 根据ID获取积分详情
     *
     * @param skuId 商品SkuId
     * @return 积分详情
     */
    PointsGoodsVO getPointsGoodsDetailBySkuId(String skuId);

}
