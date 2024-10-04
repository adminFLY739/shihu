package cn.lili.modules.order.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/6/19 15:08
 * @description: 订单数据展示
 */

@Data
public class OrderDataVO {

    @ApiModelProperty(value = "订单总数量")
    private Long orderNum;

    @ApiModelProperty(value = "待发货订单总数量")
    private Long orderUndeliverdNum;

    @ApiModelProperty(value = "待核验订单总数量")
    private Long orderTakeNum;

    @ApiModelProperty(value = "已完成订单总数量")
    private Long orderCompletedNum;
}
