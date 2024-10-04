package cn.lili.controller.card;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.card.entity.dos.CardOrder;
import cn.lili.modules.card.entity.dto.CardOrderSearchParams;
import cn.lili.modules.card.service.CardOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: nxc
 * @since: 2023/6/27 16:44
 * @description: 店铺端,卡券订单接口
 */

@RestController
@Api(tags = "店铺端,卡券订单接口")
@RequestMapping("/store/card/cardOrder")
public class CardOrderStoreController {


    @Autowired
    private CardOrderService cardOrderService;

    @GetMapping
    @ApiOperation(value = "分页获取卡券订单列表")
    public ResultMessage<IPage<CardOrder>> getCardList(CardOrderSearchParams queryParam, PageVO page) {
        IPage<CardOrder> cardOrders = cardOrderService.queryCardOrder(queryParam, page);
        return ResultUtil.data(cardOrders);
    }
}
