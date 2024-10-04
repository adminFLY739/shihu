package cn.lili.modules.wallet.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.order.trade.entity.vo.RechargeQueryVO;
import cn.lili.modules.wallet.entity.dos.Recharge;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 预存款充值业务层
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
public interface RechargeService extends IService<Recharge> {

    /**
     * 创建充值订单
     *
     * @param price 价格
     * @param tenantId 租户id
     * @return 预存款充值记录
     */
    Recharge recharge(Double price,String tenantId);

    /**
     * 查询充值订单列表
     *
     * @param page            分页数据
     * @param rechargeQueryVO 查询条件
     * @return 充值订单列表
     */
    IPage<Recharge> rechargePage(PageVO page, RechargeQueryVO rechargeQueryVO);


    /**
     * 支付成功
     *
     * @param sn            充值订单编号
     * @param receivableNo  流水no
     * @param paymentMethod 支付方式
     */
    void paySuccess(String sn, String receivableNo, String paymentMethod);

    /**
     * 根据充值订单号查询充值信息
     *
     * @param sn 充值订单号
     * @return  充值信息
     */
    Recharge getRecharge(String sn);

    /**
     * 充值订单取消
     *
     * @param sn 充值订单sn
     */
    void rechargeOrderCancel(String sn);

}
