package cn.lili.controller.order;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.enums.CartTypeEnum;
import cn.lili.modules.order.cart.entity.vo.TradeParams;
import cn.lili.modules.order.cart.service.CartService;
import cn.lili.modules.order.order.entity.vo.ReceiptVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 买家端，购物车接口
 *
 * @author Chopper
 * @since 2020/11/16 10:04 下午
 */
@Slf4j
@RestController
@Api(tags = "买家端，购物车接口")
@RequestMapping("/buyer/trade/carts")
public class CartController {

    /**
     * 购物车
     */
    @Autowired
    private CartService cartService;


    @ApiOperation(value = "向购物车中添加一个产品")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "产品ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "此产品的购买数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cartType", value = "购物车类型，默认加入购物车", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResultMessage<Object> add(@NotNull(message = "产品id不能为空") String skuId,
                                     @NotNull(message = "购买数量不能为空") @Min(value = 1, message = "加入购物车数量必须大于0") Integer num,
                                     String cartType,String tenantId) {
        try {
            //读取选中的列表
            cartService.add(skuId, num, cartType, false,tenantId);
            return ResultUtil.success();
        } catch (ServiceException se) {
            log.info(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }


    @ApiOperation(value = "获取购物车页面购物车详情")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "query")
    })
    @GetMapping("/all")
    public ResultMessage<TradeDTO> cartAll(String tenantId) {
        return ResultUtil.data(this.cartService.getAllTradeDTO(tenantId));
    }

    @ApiOperation(value = "获取购物车数量")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "checked", value = "是否选择", required = false, paramType = "query"),
      @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "query")
    })
    @GetMapping("/count")
    public ResultMessage<Long> cartCount(@RequestParam(required = false) Boolean checked,String tenantId) {
        return ResultUtil.data(this.cartService.getCartNum(checked,tenantId));
    }

    @ApiOperation(value = "获取购物车可用优惠券数量")
    @GetMapping("/coupon/num")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "query")
    })
    public ResultMessage<Long> cartCouponNum(String way,String tenantId) {
        return ResultUtil.data(this.cartService.getCanUseCoupon(CartTypeEnum.valueOf(way),tenantId));
    }

    @ApiOperation(value = "更新购物车中单个产品数量", notes = "更新购物车中的多个产品的数量或选中状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "产品id数组", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "num", value = "产品数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true , paramType = "query")
    })
    @PostMapping(value = "/sku/num/{skuId}")
    public ResultMessage<Object> update(@NotNull(message = "产品id不能为空") @PathVariable(name = "skuId") String skuId,
                                        Integer num,String tenantId) {
        cartService.add(skuId, num, CartTypeEnum.CART.name(), true,tenantId);
        return ResultUtil.success();
    }


    @ApiOperation(value = "更新购物车中单个产品", notes = "更新购物车中的多个产品的数量或选中状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "产品id数组", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "checked", value = "是否选择", paramType = "query")
    })
    @PostMapping(value = "/sku/checked/{skuId}")
    public ResultMessage<Object> updateChecked(@NotNull(message = "产品id不能为空") @PathVariable(name = "skuId") String skuId,
                                               boolean checked) {
        cartService.checked(skuId, checked);
        return ResultUtil.success();
    }


    @ApiOperation(value = "购物车选中设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checked", value = "是否选择" ,dataType = "boolean",paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id" , required = true,paramType = "query")
    })
    @PostMapping(value = "/sku/checked", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage<Object> updateAll(boolean checked,String tenantId) {
        cartService.checkedAll(checked,tenantId);
        return ResultUtil.success();
    }


    @ApiOperation(value = "批量设置某商家的商品为选中或不选中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "卖家id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "checked", value = "是否选中", required = true, dataType = "int", paramType = "query", allowableValues = "0,1"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "query"),
    })
    @ResponseBody
    @PostMapping(value = "/store/{storeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultMessage<Object> updateStoreAll(@NotNull(message = "卖家id不能为空") @PathVariable(name = "storeId") String storeId, boolean checked,String tenantId) {
        cartService.checkedStore(storeId, checked,tenantId);
        return ResultUtil.success();
    }


    @ApiOperation(value = "清空购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id" , required = true,paramType = "query")
    })
    @DeleteMapping()
    public ResultMessage<Object> clean(String tenantId) {
        cartService.clean(tenantId);
        return ResultUtil.success();
    }


    @ApiOperation(value = "删除购物车中的一个或多个产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuIds", value = "产品id", required = true, dataType = "Long", paramType = "path", allowMultiple = true),
            @ApiImplicitParam(name = "tenantId", value = "租户id" , required = true , paramType = "query")
    })
    @DeleteMapping(value = "/sku/remove")
    public ResultMessage<Object> delete(String[] skuIds,String tenantId) {
        cartService.delete(skuIds,tenantId);
        return ResultUtil.success();
    }


    @ApiOperation(value = "获取结算页面购物车详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT /卡券自提 CARD", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id" , required = true , paramType = "query")
    })
    @GetMapping("/checked")
    public ResultMessage<TradeDTO> cartChecked(@NotNull(message = "读取选中列表") String way,String tenantId) {
        try {
            //读取选中的列表
            return ResultUtil.data(this.cartService.getCheckedTradeDTO(CartTypeEnum.valueOf(way),tenantId));
        } catch (ServiceException se) {
            log.error(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }


    @ApiOperation(value = "选择收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shippingAddressId", value = "收货地址id ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "way", value = "购物车类型 ", paramType = "query"),
            @ApiImplicitParam(name = "tenantId" , value = "租户id" , required = true , paramType = "query")
    })
    @GetMapping("/shippingAddress")
    public ResultMessage<Object> shippingAddress(@NotNull(message = "收货地址ID不能为空") String shippingAddressId,
                                                 String way,String tenantId) {
        try {
            cartService.shippingAddress(shippingAddressId, way,tenantId);
            return ResultUtil.success();
        } catch (ServiceException se) {
            log.error(ResultCode.SHIPPING_NOT_APPLY.message(), se);
            throw new ServiceException(ResultCode.SHIPPING_NOT_APPLY);
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }

    @ApiOperation(value = "选择自提地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeAddressId", value = "自提地址id ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "way", value = "购物车类型 ", paramType = "query"),
            @ApiImplicitParam(name = "tenantId" , value = "租户id", paramType = "query")
    })
    @GetMapping("/storeAddress")
    public ResultMessage<Object> shippingSelfPickAddress(@NotNull(message = "自提地址ID不能为空") String storeAddressId,
                                                 String way,String tenantId) {
        try {
            cartService.shippingSelfAddress(storeAddressId, way,tenantId);
            return ResultUtil.success();
        } catch (ServiceException se) {
            log.error(ResultCode.SHIPPING_NOT_APPLY.message(), se);
            throw new ServiceException(ResultCode.SHIPPING_NOT_APPLY);
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }

    @ApiOperation(value = "选择配送方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shippingMethod", value = "配送方式：SELF_PICK_UP(自提)," +
                    "LOCAL_TOWN_DELIVERY(同城配送)," +
                    "LOGISTICS(物流) ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "way", value = "购物车类型 ", paramType = "query"),
            @ApiImplicitParam(name = "tenantId" , value = "租户id", required = true ,paramType = "query")
    })
    @PutMapping("/shippingMethod")
    public ResultMessage<Object> shippingMethod(@NotNull(message = "配送方式不能为空") String shippingMethod,
                                                String way,String tenantId) {
        try {
            cartService.shippingMethod(shippingMethod, way,tenantId);
            return ResultUtil.success();
        } catch (ServiceException se) {
            log.error(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }

    @ApiOperation(value = "获取用户可选择的物流方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "购物车类型 ", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id",required = true , paramType = "query")
    })
    @GetMapping("/shippingMethodList")
    public ResultMessage<Object> shippingMethodList(String way,String tenantId) {
        try {
            return ResultUtil.data(cartService.shippingMethodList(way,tenantId));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(ResultCode.ERROR);
        }
    }

    @ApiOperation(value = "选择发票")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id",required = true , paramType = "query")
    })
    @GetMapping("/select/receipt")
    public ResultMessage<Object> selectReceipt(String way, ReceiptVO receiptVO,String tenantId) {
        this.cartService.shippingReceipt(receiptVO, way,tenantId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "选择优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "购物车购买：CART/立即购买：BUY_NOW/拼团购买：PINTUAN / 积分购买：POINT ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "memberCouponId", value = "优惠券id ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "used", value = "使用true 弃用false ", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id ", required = true, paramType = "query")

    })
    @GetMapping("/select/coupon")
    public ResultMessage<Object> selectCoupon(String way, @NotNull(message = "优惠券id不能为空") String memberCouponId, boolean used,String tenantId) {
        this.cartService.selectCoupon(memberCouponId, way, used,tenantId);
        return ResultUtil.success();
    }


    @PreventDuplicateSubmissions
    @ApiOperation(value = "创建交易")
    @PostMapping(value = "/create/trade", consumes = "application/json", produces = "application/json")
    public ResultMessage<Object> crateTrade(@RequestBody TradeParams tradeParams) {
        try {
            //读取选中的列表
            return ResultUtil.data(this.cartService.createTrade(tradeParams));
        } catch (ServiceException se) {
            log.info(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.ORDER_ERROR.message(), e);
            throw e;
        }
    }
}
