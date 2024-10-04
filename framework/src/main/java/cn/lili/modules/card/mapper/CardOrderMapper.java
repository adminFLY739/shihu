package cn.lili.modules.card.mapper;

import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.CardOrder;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/6/27 09:50
 * @description: 卡券订单数据处理层
 */
public interface CardOrderMapper extends BaseMapper<CardOrder> {



    /**
     * 查询订单
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 卡券券分页
     */
    @Select("select * from li_card_order ${ew.customSqlSegment} ")
    IPage<CardOrder> queryCardOrder(IPage<CardOrder> page, @Param(Constants.WRAPPER) Wrapper<Card> queryWrapper);
}
