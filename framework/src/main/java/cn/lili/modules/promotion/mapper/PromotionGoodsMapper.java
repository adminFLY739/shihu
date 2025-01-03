package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import cn.lili.modules.promotion.entity.dos.SeckillApply;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 促销商品数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface PromotionGoodsMapper extends BaseMapper<PromotionGoods> {


    /**
     * 查询参加活动促销商品是否同时参加指定类型的活动
     *
     * @param promotionType 促销类型
     * @param skuId         skuId
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 共参加了几种活动
     */
    @Select("select count(0) from li_promotion_goods where promotion_type = #{promotionType} and sku_id = #{skuId} and (" +
            "( start_time < #{startTime}  && end_time > #{startTime} ) || ( start_time < #{endTime}  && end_time > #{endTime} ) || " +
            "( start_time < #{startTime}  && end_time > #{endTime} ) || ( start_time > #{startTime}  && end_time < #{endTime} )" +
            ")")
    Integer selectInnerOverlapPromotionGoods(@Param("promotionType") String promotionType,
                                             @Param("skuId") String skuId,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime);

    /**
     * 查询参加活动促销商品是否同时参加指定类型的活动
     *
     * @param promotionType 促销类型
     * @param skuId         skuId
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param promotionId   促销活动ID
     * @return 共参加了几种活动
     */
    @Select("select count(0) from li_promotion_goods where promotion_type = #{promotionType} and sku_id = #{skuId} and (" +
            "( start_time < #{startTime}  && end_time > #{startTime} ) || ( start_time < #{endTime}  && end_time > #{endTime} ) || " +
            "( start_time < #{startTime}  && end_time > #{endTime} ) || ( start_time > #{startTime}  && end_time < #{endTime} )" +
            ") and promotion_id != #{promotionId}")
    Integer selectInnerOverlapPromotionGoodsWithout(@Param("promotionType") String promotionType,
                                                    @Param("skuId") String skuId,
                                                    @Param("startTime") Date startTime,
                                                    @Param("endTime") Date endTime,
                                                    @Param("promotionId") String promotionId);

    /**
     * 查询参加活动促销商品价格
     *
     * @param queryWrapper 查询条件
     * @return 共参加了几种活动
     */
    @Select("select price from li_promotion_goods ${ew.customSqlSegment} ")
    Double selectPromotionsGoodsPrice(@Param(Constants.WRAPPER) Wrapper<PromotionGoods> queryWrapper);



    /**
     * 查询根据goodsId商品
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 促销商品分页查询
     */
    @Select("select * from li_promotion_goods as me join li_goods as g on me.goods_id = g.id ${ew.customSqlSegment} ")
    IPage<PromotionGoods> queryPromotionGoods(IPage<PromotionGoods> page, @Param(Constants.WRAPPER) Wrapper<PromotionGoods> queryWrapper);

    @Delete("Delete from li_promotion_goods as me ${ew.customSqlSegment}")
    void removePromotionGoods(@Param(Constants.WRAPPER) Wrapper<PromotionGoods> queryWrapper);


  /**
   * 查询根据skuId商品
   *
   * @param queryWrapper 查询条件
   * @return 促销商品分页查询
   */
  @Select("select * from li_promotion_goods as me  ${ew.customSqlSegment} ")
  List<PromotionGoods> queryPromotionGoodsBySkuId(@Param(Constants.WRAPPER) Wrapper<PromotionGoods> queryWrapper);
}
