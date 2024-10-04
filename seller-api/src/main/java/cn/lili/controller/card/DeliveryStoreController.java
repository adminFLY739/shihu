package cn.lili.controller.card;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.card.entity.dto.DeliveryImportDTO;
import cn.lili.modules.card.entity.dto.DeliverySearchParams;
import cn.lili.modules.card.entity.vo.DeliveryVO;
import cn.lili.modules.card.service.DeliveryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Objects;

/**
 * @author: nxc
 * @since: 2023/6/19 10:05
 * @description: 提货码接口
 */

@RestController
@Api(tags = "店铺端,提货码接口")
@RequestMapping("/store/card/delivery")
public class DeliveryStoreController {

    @Autowired
    private DeliveryService deliveryService;


    @GetMapping
    @ApiOperation(value = "获取提货码列表")
    public ResultMessage<IPage<DeliveryVO>> getDeliveryList(DeliverySearchParams queryParam, PageVO page) {
        IPage<DeliveryVO> cards = deliveryService.queryDelivery(queryParam, page);
        return ResultUtil.data(cards);
    }

    @ApiOperation(value = "添加提货码")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResultMessage<Objects> addDelivery(@RequestBody DeliveryImportDTO deliveryImportDTO) {
        if (deliveryService.saveDelivery(deliveryImportDTO)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultCode.CARD_SAVE_ERROR);
    }

    @ApiOperation(value = "修改提货码状态")
    @PutMapping("/status")
    public ResultMessage<Objects> changeDeliveryStauts(String deliveryIds, String status) {
        if (deliveryService.changeDeliveryStauts(deliveryIds, status)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultCode.DELIVERY_STATUS_ERROR);
    }




}
