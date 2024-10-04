package cn.lili.controller.card;

import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.card.entity.dto.CardSearchParams;
import cn.lili.modules.card.entity.dto.DeliverySearchParams;
import cn.lili.modules.card.entity.vo.CardInfo;
import cn.lili.modules.card.entity.vo.CardVO;
import cn.lili.modules.card.entity.vo.DeliveryVO;
import cn.lili.modules.card.service.CardOrderService;
import cn.lili.modules.card.service.CardService;
import cn.lili.modules.card.service.DeliveryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: nxc
 * @since: 2023/6/20 09:29
 * @description: 用户端卡券接口
 */


@Slf4j
@Api(tags = "买家端,卡券接口")
@RestController
@RequestMapping("/buyer/card/card")
public class CardBuyerController {


    /**
     * 提货吗
     */
    @Autowired
    private DeliveryService deliveryService;

    /**
     * 卡券
     */
    @Autowired
    private CardService cardService;


    /**
     * 卡券订单
     */
    @Autowired
    private CardOrderService cardOrderService;


    @GetMapping
    @ApiOperation(value = "分页获取卡券列表")
    public ResultMessage<IPage<CardVO>> getCardList(CardSearchParams queryParam, PageVO page) {
        IPage<CardVO> cards = cardService.queryCard(queryParam, page);
        return ResultUtil.data(cards);
    }

    @GetMapping("/delivery")
    @ApiOperation(value = "获取提货码列表")
    public ResultMessage<IPage<DeliveryVO>> getDeliveryList(DeliverySearchParams queryParam, PageVO page) {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        queryParam.setMemberId(tokenUser.getId());
        IPage<DeliveryVO> cards = deliveryService.queryDelivery(queryParam, page);
        return ResultUtil.data(cards);
    }

    @ApiOperation(value = "获取卡券")
    @GetMapping("/{id}")
    public ResultMessage<CardVO> getCard(@PathVariable String id) {
        return ResultUtil.data(cardService.getDetail(id));
    }


    @ApiOperation(value = "卡券认证接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deliveryCode", value = "卡券编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deliveryPassword", value = "卡券密码", required = true, paramType = "query")
    })
    @PostMapping("/cardAuth")
    public ResultMessage<Object> cardAuth(@NotNull(message = "卡券编码不能为空") @RequestParam String deliveryCode,
                                           @NotNull(message = "卡券密码不能为空") @RequestParam String deliveryPassword) {

        if(deliveryService.cardAuth(deliveryCode,deliveryPassword)){
            return ResultUtil.success();
        }
        else{
            throw new ServiceException(ResultCode.DELIVERY_AUTH_ERROR);
        }
    }

    @ApiOperation(value = "获取卡券")
    @GetMapping("/cardInfo")
    public ResultMessage<CardInfo> getCardInfo(@NotNull(message = "卡券编码不能为空") @RequestParam String deliveryCode,
                                               @NotNull(message = "卡券密码不能为空") @RequestParam String deliveryPassword) {
        if(deliveryService.cardAuth(deliveryCode,deliveryPassword)){
            return ResultUtil.data(deliveryService.getCardInfo(deliveryCode));
        }
        else{
            throw new ServiceException(ResultCode.DELIVERY_AUTH_ERROR);
        }
    }

    @ApiOperation(value = "向卡券添加一个产品")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "产品ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "此产品的购买数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cartType", value = "购物车类型，默认加入卡券购物车", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "query")
    })
    public ResultMessage<Object> add(@NotNull(message = "产品id不能为空") String skuId,
                                     @NotNull(message = "购买数量不能为空") @Min(value = 1, message = "加入购物车数量必须大于0") Integer num,
                                     String cartType,String tenantId) {
        try {
            //读取选中的列表
            cardService.add(skuId, num, cartType, false,tenantId);
            return ResultUtil.success();
        } catch (ServiceException se) {
            log.info(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.CART_ERROR.message(), e);
            throw new ServiceException(ResultCode.CART_ERROR);
        }
    }

    @ApiOperation(value = "清空购物车")
    @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "query")
    @DeleteMapping()
    public ResultMessage<Object> clean(String tenantId) {
        cardService.clean(tenantId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "提取卡券")
    @GetMapping("useCard/{id}")
    public ResultMessage<Object> useCard(@PathVariable String id) {
       if(cardService.useCard(id)){
           return ResultUtil.success();
       }
       else{
           return ResultUtil.error(ResultCode.CARD_USE_ERROR);
       }
    }


    @PreventDuplicateSubmissions
    @ApiOperation(value = "创建卡券交易")
    @PostMapping(value = "/create/trade/{cardId}", consumes = "application/json", produces = "application/json")
    public ResultMessage<Object> crateTrade(@PathVariable String cardId) {
        try {
            //读取选中的列表
            return ResultUtil.data(cardOrderService.createCardOrder(cardId));
        } catch (ServiceException se) {
            log.info(se.getMsg(), se);
            throw se;
        } catch (Exception e) {
            log.error(ResultCode.ORDER_ERROR.message(), e);
            throw e;
        }
    }

}
