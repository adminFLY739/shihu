package cn.lili.modules.wallet.mapper;


import cn.lili.modules.wallet.entity.dos.MemberWithdrawApply;
import cn.lili.modules.wallet.entity.dos.Recharge;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 会员提现申请数据处理层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface MemberWithdrawApplyMapper extends BaseMapper<MemberWithdrawApply> {

    /**
     * 用户充值记录分页
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 会员评价分页
     */
    @Select("select me.*  FROM li_member_withdraw_apply as me   ${ew.customSqlSegment} ")
    IPage<MemberWithdrawApply> getMemberWithdrawApplyList(IPage<Recharge> page, @Param(Constants.WRAPPER) Wrapper<MemberWithdrawApply> queryWrapper);

}
