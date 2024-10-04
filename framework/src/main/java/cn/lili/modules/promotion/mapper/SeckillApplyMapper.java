package cn.lili.modules.promotion.mapper;

import cn.lili.modules.goods.entity.dos.Specification;
import cn.lili.modules.goods.entity.vos.CategoryBrandVO;
import cn.lili.modules.promotion.entity.dos.Seckill;
import cn.lili.modules.promotion.entity.dos.SeckillApply;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 秒杀活动申请数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface SeckillApplyMapper extends BaseMapper<SeckillApply> {

    /**
     * 查询秒杀活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_seckill_apply as me join li_goods_sku as gs on me.sku_id = gs.id ${ew.customSqlSegment} ")
    IPage<SeckillApply> querySeckillApply(IPage<SeckillApply> page, @Param(Constants.WRAPPER) Wrapper<SeckillApply> queryWrapper);

    /**
     * 用户根据租户获取当前时间段的秒杀商品
     * @param seckillId  秒杀id
     * @param tenantId   租户id
     * @return  用户当前租户时间段的秒杀商品
     */
    @Select("SELECT me.* FROM li_seckill_apply as me join li_goods_sku as gs on me.sku_id = gs.id and me.seckill_id = #{seckillId} and gs.tenant_id = #{tenantId} where me.delete_flag = 0")
    List<SeckillApply> seckillApplyList(String seckillId,String tenantId);
}
