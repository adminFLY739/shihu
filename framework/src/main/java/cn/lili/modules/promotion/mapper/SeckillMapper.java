package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.FullDiscount;
import cn.lili.modules.promotion.entity.dos.Seckill;
import cn.lili.modules.promotion.entity.vos.SeckillVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀活动数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface SeckillMapper extends BaseMapper<Seckill> {

    /**
     * 修改秒杀活动数量
     *
     * @param seckillId 秒杀活动ID
     */
    @Update("UPDATE li_seckill SET goods_num =( SELECT count( id ) FROM li_seckill_apply WHERE seckill_id = #{seckillId} ) WHERE id = #{seckillId}")
    void updateSeckillGoodsNum(String seckillId);


    /**
     * 查询秒杀活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select me.*  from li_seckill as me ${ew.customSqlSegment} ")
    IPage<SeckillVO> querySeckill(IPage<SeckillVO> page, @Param(Constants.WRAPPER) Wrapper<SeckillVO> queryWrapper);

    /**
     * 查询秒杀活动
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select se.*  from li_store_tenant as me join li_seckill as se on se.tenant_id = me.tenant_id ${ew.customSqlSegment} ")
    IPage<Seckill> storeQuerySeckill(IPage<Seckill> page, @Param(Constants.WRAPPER) Wrapper<Seckill> queryWrapper);
}
