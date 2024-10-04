package cn.lili.modules.wallet.mapper;


import cn.lili.modules.wallet.entity.dos.Recharge;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 预存款充值记录数据处理层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface RechargeMapper extends BaseMapper<Recharge> {

    /**
     * 用户充值记录分页
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 会员评价分页
     */
    @Select("select * FROM li_recharge ${ew.customSqlSegment} ")
    IPage<Recharge> getRechargeList(IPage<Recharge> page, @Param(Constants.WRAPPER) Wrapper<Recharge> queryWrapper);

}
