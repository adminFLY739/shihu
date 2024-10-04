package cn.lili.modules.goods.mapper;

import cn.lili.modules.goods.entity.dos.Shipments;
import cn.lili.modules.goods.entity.vos.GoodsVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/9/7 09:00
 * @description: 发货数据处理层
 */
public interface ShipmentsMapper extends BaseMapper<Shipments> {

  /**
   * 查询商品列表
   *
   * @param page         分页
   * @param queryWrapper 查询条件
   * @return 订单支付记录分页
   */
  @Select("select * from li_shipments ${ew.customSqlSegment} ")
  IPage<Shipments> queryShipmentList(IPage<GoodsVO> page, @Param(Constants.WRAPPER) Wrapper<Shipments> queryWrapper);
}
