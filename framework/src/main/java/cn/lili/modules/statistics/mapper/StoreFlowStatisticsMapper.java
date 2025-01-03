package cn.lili.modules.statistics.mapper;

import cn.lili.modules.order.order.entity.dos.StoreFlow;
import cn.lili.modules.statistics.entity.vo.CategoryStatisticsDataVO;
import cn.lili.modules.statistics.entity.vo.GoodsStatisticsDataVO;
import cn.lili.modules.statistics.entity.vo.StoreStatisticsDataVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品统计数据处理层
 *
 * @author Bulbasaur
 * @since 2020/11/17 7:34 下午
 */
public interface StoreFlowStatisticsMapper extends BaseMapper<StoreFlow> {

    /**
     * 商品统计
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 商品统计列表
     */
    @Select("SELECT goods_id,me.goods_name,SUM(final_price) AS price,SUM(num) AS num FROM li_store_flow as me join li_goods as g on me.goods_id = g.id ${ew.customSqlSegment}")
    List<GoodsStatisticsDataVO> getGoodsStatisticsData(IPage<GoodsStatisticsDataVO> page, @Param(Constants.WRAPPER) Wrapper<GoodsStatisticsDataVO> queryWrapper);

    /**
     * 分类统计
     *
     * @param queryWrapper 查询条件
     * @return 分类统计列表
     */
    @Select("SELECT category_id,category_name,SUM(price) AS price,SUM(num) AS num FROM li_store_flow as me ${ew.customSqlSegment}")
    List<CategoryStatisticsDataVO> getCateGoryStatisticsData(@Param(Constants.WRAPPER) Wrapper<CategoryStatisticsDataVO> queryWrapper);


    /**
     * 店铺统计列表
     *
     * @param page         分页
     * @param queryWrapper 查询参数
     * @return 店铺统计列表
     */
    @Select("SELECT me.store_id AS storeId,me.store_name AS storeName,SUM(final_price) AS price,SUM(num) AS num FROM li_store_flow as me join li_goods as g on me.goods_id = g.id ${ew.customSqlSegment}")
    List<StoreStatisticsDataVO> getStoreStatisticsData(IPage<GoodsStatisticsDataVO> page, @Param(Constants.WRAPPER) Wrapper<GoodsStatisticsDataVO> queryWrapper);

    /**
     * 店铺统计付款人数
     *
     * @param storeId   店铺id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 付款人数
     */
    @Select("SELECT count(0) AS num FROM (SELECT count(0) FROM li_store_flow as me join li_goods as g on me.goods_id = g.id" +
            " where me.store_id = #{storeId} and flow_type='PAY' and me.create_time >=#{startTime} and me.create_time < #{endTime} and tenant_id = #{tenantId}" +
            " GROUP BY member_id) t")
    Long countPayersByStore(String storeId, Date startTime, Date endTime);

    /**
     * 统计付款人数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 付款人数
     */
    @Select("SELECT count(0) AS num FROM (SELECT count(0) FROM li_store_flow " +
            " where  flow_type='PAY' and create_time >=#{startTime} and create_time < #{endTime}" +
            " GROUP BY member_id) t")
    Long countPayers(Date startTime, Date endTime);

    /**
     * 订单数量，和总资金
     *
     * @param queryWrapper 查询条件
     * @return 会员评价
     */
    @Select("select SUM(final_price) AS price ,COUNT(0) AS num ,count(DISTINCT member_id) AS memberNum from li_store_flow as me join li_goods as g on me.goods_id = g.id ${ew.customSqlSegment}")
    List<Map<String, Object>> getOrderStatisticsPrice(@Param(Constants.WRAPPER) Wrapper<StoreFlow> queryWrapper);



}
