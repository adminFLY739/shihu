package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.FullDiscount;
import cn.lili.modules.promotion.entity.dos.Pintuan;
import cn.lili.modules.promotion.entity.vos.PintuanVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 拼团数据处理层
 *
 * @author Chopper
 * @since 2020/8/21
 */
public interface PintuanMapper extends BaseMapper<Pintuan> {

    /**
     * 查询满额
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select me.* , tenant.name from li_store_tenant as st join li_store as s on st.store_id = s.id join li_pintuan as me on me.store_id = s.id join tenant on st.tenant_id = tenant.id ${ew.customSqlSegment} ")
    IPage<PintuanVO> queryPintuan(IPage<PintuanVO> page, @Param(Constants.WRAPPER) Wrapper<PintuanVO> queryWrapper);

    /**
     * 查询满额
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_pintuan as me ${ew.customSqlSegment} ")
    IPage<Pintuan> storeQueryPintuan(IPage<Pintuan> page, @Param(Constants.WRAPPER) Wrapper<Pintuan> queryWrapper);

}
