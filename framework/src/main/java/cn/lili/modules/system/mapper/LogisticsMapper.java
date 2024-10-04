package cn.lili.modules.system.mapper;

import cn.lili.modules.goods.entity.vos.GoodsVO;
import cn.lili.modules.system.entity.dos.Logistics;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 物流公司数据处理层
 *
 * @author Chopper
 * @since 2020/11/17 8:01 下午
 */
public interface LogisticsMapper extends BaseMapper<Logistics> {

  /**
   * 查询商品列表
   *
   * @param page         分页
   * @param queryWrapper 查询条件
   * @return 订单支付记录分页
   */
  @Select("select * from li_logistics ${ew.customSqlSegment} ")
  IPage<Logistics>  queryLogisticsList(IPage<Logistics>  page, @Param(Constants.WRAPPER) Wrapper<Logistics> queryWrapper);

}
