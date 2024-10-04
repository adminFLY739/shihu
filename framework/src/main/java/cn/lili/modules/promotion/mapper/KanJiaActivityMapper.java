package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.KanjiaActivity;
import cn.lili.modules.promotion.entity.vos.kanjia.KanjiaActivityGoodsVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * 砍价活动参与记录 数据处理层
 *
 * @author qiuqiu
 * @date 2021/7/1
 */
public interface KanJiaActivityMapper extends BaseMapper<KanjiaActivity> {

  /**
   * 查询砍价记录
   *
   * @param page         分页
   * @param queryWrapper 查询条件
   * @return 砍价商品记录分页
   */
  @Select("select me.* from li_kanjia_activity as me join li_goods_sku as g on me.sku_id = g.id  ${ew.customSqlSegment} ")
  IPage<KanjiaActivity> queryKanjiaActivityGoods(IPage<KanjiaActivity> page, @Param(Constants.WRAPPER) Wrapper<KanjiaActivity> queryWrapper);

}
