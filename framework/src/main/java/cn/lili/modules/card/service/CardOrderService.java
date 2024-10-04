package cn.lili.modules.card.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.CardOrder;
import cn.lili.modules.card.entity.dto.CardOrderSearchParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: nxc
 * @since: 2023/6/27 09:50
 * @description: 卡券订单业务逻辑层
 */
public interface CardOrderService extends IService<CardOrder> {

    /**
    *
    * 创建卡券交易
    *
    *@Param: cardId 卡券id
    *@return: 卡券交易信息
    *@Author: nxc
    *@date: 2023/6/27
    */
    CardOrder createCardOrder(String cardId);

    /**
     * 根据sn获取卡券订单
     * @param sn 订单号
     * @return 卡券订单
     */
    CardOrder getBySn(String sn);

    /**
     * 订单付款
     * 修改订单付款信息
     * 记录订单流水
     *
     * @param orderSn       订单编号
     * @param paymentMethod 支付方法
     * @param receivableNo  第三方流水
     */
    void payOrder(String orderSn, String paymentMethod, String receivableNo);

    /**
     * 查询卡券订单
     *
     * @param page         分页
     * @param queryParam 查询条件
     * @return 卡券分页
     */
    IPage<CardOrder> queryCardOrder(CardOrderSearchParams queryParam, PageVO page);
}
