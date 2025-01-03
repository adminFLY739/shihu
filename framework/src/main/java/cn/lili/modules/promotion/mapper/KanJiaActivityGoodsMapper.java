package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.KanjiaActivityGoods;
import cn.lili.modules.promotion.entity.dos.Pintuan;
import cn.lili.modules.promotion.entity.vos.kanjia.KanjiaActivityGoodsListVO;
import cn.lili.modules.promotion.entity.vos.kanjia.KanjiaActivityGoodsVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * 砍价活动商品数据处理层
 *
 * @author qiuqiu
 * @date 2021/7/1
 */
public interface KanJiaActivityGoodsMapper extends BaseMapper<KanjiaActivityGoods> {

    /**
     * 获取砍价商品VO分页
     * @param page 分页
     * @param queryWrapper 查询条件
     * @return 砍价商品VO分页
     */
    @Select("SELECT me.* FROM li_kanjia_activity_goods as me join li_goods as g on me.goods_id = g.id ${ew.customSqlSegment}")
    IPage<KanjiaActivityGoodsListVO> kanjiaActivityGoodsVOPage(IPage<KanjiaActivityGoods> page, @Param(Constants.WRAPPER) Wrapper<KanjiaActivityGoods> queryWrapper);

    /**
     * 查询砍价记录
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 砍价商品记录分页
     */
    @Select("select me.* ,tenant.name from li_kanjia_activity_goods as me join li_goods as g on me.goods_id = g.id join tenant on g.tenant_id = tenant.id ${ew.customSqlSegment} ")
    IPage<KanjiaActivityGoodsVO> queryKanjiaActivityGoods(IPage<KanjiaActivityGoodsVO> page, @Param(Constants.WRAPPER) Wrapper<KanjiaActivityGoodsVO> queryWrapper);

}
