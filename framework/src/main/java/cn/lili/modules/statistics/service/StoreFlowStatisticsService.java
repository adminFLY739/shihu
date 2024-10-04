package cn.lili.modules.statistics.service;

import cn.lili.modules.order.order.entity.dos.StoreFlow;
import cn.lili.modules.statistics.entity.dto.GoodsStatisticsQueryParam;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import cn.lili.modules.statistics.entity.vo.CategoryStatisticsDataVO;
import cn.lili.modules.statistics.entity.vo.GoodsStatisticsDataVO;
import cn.lili.modules.statistics.entity.vo.OrderOverviewVO;
import cn.lili.modules.statistics.entity.vo.StoreStatisticsDataVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流水统计业务层
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:06
 */
public interface StoreFlowStatisticsService extends IService<StoreFlow> {

    /**
     * 查询热卖商品
     * 查询TOP100的商品
     *
     * @param goodsStatisticsQueryParam 查询参数
     * @param num                       数量
     * @return 热卖商品
     */
    List<GoodsStatisticsDataVO> getGoodsStatisticsData(GoodsStatisticsQueryParam goodsStatisticsQueryParam, Integer num);

    /**
     * 查询行业统计
     * 根据商品一级分类ID查询
     *
     * @param goodsStatisticsQueryParam 查询参数
     * @return 热门分类
     */
    List<CategoryStatisticsDataVO> getCategoryStatisticsData(GoodsStatisticsQueryParam goodsStatisticsQueryParam);

    /**
     * 店铺流水 根据店铺 统计
     *
     * @param page  分页参数
     * @param queryWrapper  查询参数
     * @return 流水
     */
    List<StoreStatisticsDataVO> getStoreStatisticsData(Page page, QueryWrapper queryWrapper);

    /**
     * 查询今日付款统计
     *
     * @return 订单统计金额
     */
    Map<String, Object> getOrderStatisticsPrice(String tenantId);

    /**
     * 订单统计，数据概览
     *
     * @param dates  日期
     * @param orderOverviewVO 订单
     * @param statisticsQueryParam 查找参数
     */
    void overview(Date[] dates, OrderOverviewVO orderOverviewVO, StatisticsQueryParam statisticsQueryParam);
}
