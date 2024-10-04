package cn.lili.modules.goods.mapper;

import cn.lili.modules.goods.entity.dos.DraftGoods;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 草稿商品数据处理层
 *
 * @author paulG
 * @since 2020/12/19
 **/
public interface DraftGoodsMapper extends BaseMapper<DraftGoods> {

    /**
     * 查询商品模板列表
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_draft_goods as me  ${ew.customSqlSegment} ")
    IPage<DraftGoods> queryDraftGoods(IPage<DraftGoods> page, @Param(Constants.WRAPPER) Wrapper<DraftGoods> queryWrapper);
}
