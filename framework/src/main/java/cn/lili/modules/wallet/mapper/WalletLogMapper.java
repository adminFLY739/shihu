package cn.lili.modules.wallet.mapper;

import cn.lili.modules.wallet.entity.dos.WalletLog;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 预存款日志数据处理层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface WalletLogMapper extends BaseMapper<WalletLog> {

    /**
     * 用户存款日志分页
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 会员评价分页
     */
    @Select("select * FROM li_wallet_log ${ew.customSqlSegment} ")
    IPage<WalletLog> getWalletLogList(IPage<WalletLog> page, @Param(Constants.WRAPPER) Wrapper<WalletLog> queryWrapper);

}
