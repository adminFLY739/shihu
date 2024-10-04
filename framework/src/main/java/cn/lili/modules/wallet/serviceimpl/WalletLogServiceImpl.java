package cn.lili.modules.wallet.serviceimpl;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.order.trade.entity.vo.DepositQueryVO;
import cn.lili.modules.wallet.entity.dos.WalletLog;
import cn.lili.modules.wallet.mapper.WalletLogMapper;
import cn.lili.modules.wallet.service.WalletLogService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 预存款日志业务层实现
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Service
public class WalletLogServiceImpl extends ServiceImpl<WalletLogMapper, WalletLog> implements WalletLogService {
    @Resource
    WalletLogMapper walletLogMapper;

    @Override
    public IPage<WalletLog> depositLogPage(PageVO page, DepositQueryVO depositQueryVO) {

        //查询返回数据
        return walletLogMapper.getWalletLogList(PageUtil.initPage(page), depositQueryVO.queryWrapper());
    }
}
