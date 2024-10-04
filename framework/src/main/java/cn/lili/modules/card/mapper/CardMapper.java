package cn.lili.modules.card.mapper;

import cn.lili.modules.card.entity.dos.Card;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/6/17 09:56
 * @description: 卡券数据处理层
 */
public interface CardMapper extends BaseMapper<Card> {


    /**
     * 查询卡券
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 卡券券分页
     */
    @Select("select me.* , tenant.name from li_store_tenant as st join li_store as s on st.store_id = s.id join li_card as me on me.store_id = s.id join tenant on st.tenant_id = tenant.id ${ew.customSqlSegment} ")
    IPage<Card> queryCard(IPage<Card> page, @Param(Constants.WRAPPER) Wrapper<Card> queryWrapper);
}
