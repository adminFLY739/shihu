package cn.lili.modules.order.cart.render;

import cn.lili.common.exception.ServiceException;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.enums.CartTypeEnum;
import cn.lili.modules.order.cart.entity.enums.RenderStepEnums;
import cn.lili.modules.order.cart.service.CartService;
import cn.lili.modules.order.order.entity.dos.Trade;
import cn.lili.modules.order.order.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易构造&&创建
 *
 * @author Chopper
 * @since2020-04-01 9:47 下午
 */
@Service
@Slf4j
public class TradeBuilder {

    /**
     * 购物车渲染步骤
     */
    @Autowired
    private List<CartRenderStep> cartRenderSteps;
    /**
     * 交易
     */
    @Autowired
    private TradeService tradeService;
    /**
     * 购物车业务
     */
    @Autowired
    private CartService cartService;


    /**
     * 构造购物车
     * 购物车与结算信息不一致的地方主要是优惠券计算和运费计算，其他规则都是一致都
     *
     * @param checkedWay 购物车类型
     * @return 购物车展示信息
     */
    public TradeDTO buildCart(CartTypeEnum checkedWay,String tenantId) {
        //读取对应购物车的商品信息
        TradeDTO tradeDTO = cartService.readDTO(checkedWay,tenantId);

        //购物车需要将交易中的优惠券取消掉
        if (checkedWay.equals(CartTypeEnum.CART)) {
            tradeDTO.setStoreCoupons(null);
            tradeDTO.setPlatformCoupon(null);
        }

        //按照计划进行渲染
        renderCartBySteps(tradeDTO, RenderStepStatement.cartRender);
        return tradeDTO;
    }

    /**
     * 构造结算页面
     */
    public TradeDTO buildChecked(CartTypeEnum checkedWay,String tenantId) {
        //读取对应购物车的商品信息
        TradeDTO tradeDTO = cartService.readDTO(checkedWay,tenantId);
        //需要对购物车渲染
        if (isSingle(checkedWay)) {
            renderCartBySteps(tradeDTO, RenderStepStatement.checkedSingleRender);
        } else if (checkedWay.equals(CartTypeEnum.PINTUAN)) {
            renderCartBySteps(tradeDTO, RenderStepStatement.pintuanTradeRender);
        }else if(checkedWay.equals(CartTypeEnum.CARD)) {
            renderCartBySteps(tradeDTO, RenderStepStatement.cardTradeRender);
        }
        else {
            renderCartBySteps(tradeDTO, RenderStepStatement.checkedRender);
        }

        return tradeDTO;
    }

    /**
     * 创建一笔交易
     * 1.构造交易
     * 2.创建交易
     *
     * @param tradeDTO 交易模型
     * @return 交易信息
     */
    public Trade createTrade(TradeDTO tradeDTO) {

        //需要对购物车渲染
        if (isSingle(tradeDTO.getCartTypeEnum())) {
            renderCartBySteps(tradeDTO, RenderStepStatement.singleTradeRender);
        } else if (tradeDTO.getCartTypeEnum().equals(CartTypeEnum.PINTUAN)) {
            renderCartBySteps(tradeDTO, RenderStepStatement.pintuanTradeRender);
        }else if(tradeDTO.getCartTypeEnum().equals(CartTypeEnum.CARD)) {
            renderCartBySteps(tradeDTO, RenderStepStatement.cardTradeRender);
        }
        else {
            renderCartBySteps(tradeDTO, RenderStepStatement.tradeRender);
        }

        //添加order订单及order_item子订单并返回
        return tradeService.createTrade(tradeDTO);
    }

    /**
     * 是否为单品渲染
     *
     * @param checkedWay 购物车类型
     * @return 返回是否单品
     */
    private boolean isSingle(CartTypeEnum checkedWay) {
        //拼团   积分   砍价商品

        return (checkedWay.equals(CartTypeEnum.POINTS) || checkedWay.equals(CartTypeEnum.KANJIA));
    }

    /**
     * 根据渲染步骤，渲染购物车信息
     *
     * @param tradeDTO      交易DTO
     * @param defaultRender 渲染枚举
     */
    private void renderCartBySteps(TradeDTO tradeDTO, RenderStepEnums[] defaultRender) {
        // 遍历指定的默认渲染步骤
        for (RenderStepEnums step : defaultRender) {
            // 遍历购物车渲染步骤列表
            for (CartRenderStep render : cartRenderSteps) {
                try {
                    // 如果当前渲染步骤与指定的默认渲染步骤相等
                    if (render.step().equals(step)) {
                        // 执行渲染操作，传入交易DTO作为参数
                        render.render(tradeDTO);
                    }
                } catch (ServiceException e) {
                    // 如果捕获到自定义的ServiceException异常，则直接抛出
                    throw e;
                } catch (Exception e) {
                    // 捕获其他异常，并记录日志
                    log.error("购物车{}渲染异常：", render.getClass(), e);
                }
            }
        }
    }

}
