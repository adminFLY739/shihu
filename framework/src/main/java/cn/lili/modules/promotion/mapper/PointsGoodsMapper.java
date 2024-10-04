package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.Pintuan;
import cn.lili.modules.promotion.entity.dos.PointsGoods;
import cn.lili.modules.promotion.entity.vos.PointsGoodsVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分商品数据处理层
 *
 * @author paulG
 * @since 2020/8/21
 **/
public interface PointsGoodsMapper extends BaseMapper<PointsGoods> {



    /**
     * 查询满额
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select me.* , tenant.name from li_points_goods as me join li_goods as g on me.goods_id = g.id join tenant on g.tenant_id = tenant.id  ${ew.customSqlSegment} ")
    IPage<PointsGoodsVO> queryPointsGoods(IPage<PointsGoodsVO> page, @Param(Constants.WRAPPER) Wrapper<PointsGoodsVO> queryWrapper);

}
