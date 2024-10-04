package cn.lili.modules.card.mapper;

import cn.lili.modules.card.entity.dos.Delivery;
import cn.lili.modules.card.entity.vo.DeliveryVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/6/19 09:20
 * @description: 提货码数据处理层
 */
public interface DeliveryMapper extends BaseMapper<Delivery> {


    /**
     * 查询卡券
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 卡券券分页
     */
    @Select("select * from li_delivery as me join li_card as c on me.card_id = c.id ${ew.customSqlSegment} ")
    IPage<DeliveryVO> queryDelivery(IPage<Delivery> page, @Param(Constants.WRAPPER) Wrapper<Delivery> queryWrapper);
}
