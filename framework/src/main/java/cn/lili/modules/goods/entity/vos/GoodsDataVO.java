package cn.lili.modules.goods.entity.vos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/6/19 16:01
 * @description: 商品数据vo
 */

@Data
public class GoodsDataVO {

    @ApiModelProperty(value = "商品总数量")
    private Long goodsNum;

    @ApiModelProperty(value = "待处理商品审核")
    private Long goodsAudit;

    @ApiModelProperty(value = "待上架商品")
    private Long goodsUpper;


}
