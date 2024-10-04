package cn.lili.modules.statistics.mapper;

import cn.lili.modules.order.aftersale.entity.dos.AfterSale;
import cn.lili.modules.order.aftersale.entity.vo.AfterSaleVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 售后统计数据处理层
 *
 * @author Bulbasaur
 * @since 2020/11/17 7:34 下午
 */
public interface AfterSaleStatisticsMapper extends BaseMapper<AfterSale> {

  @Select("SELECT * FROM li_after_sale as me join li_goods as m on me.goods_id = m.id ${ew.customSqlSegment}")
  IPage<AfterSale> getAfterSaleStatistics(IPage<AfterSaleVO> page, @Param(Constants.WRAPPER) Wrapper<AfterSale> queryWrapper);

}
