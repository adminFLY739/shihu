package cn.lili.modules.goods.entity.vos;

import cn.lili.modules.goods.entity.dos.Commodity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 直播商品VO
 *
 * @author Bulbasaur
 * @since 2021/5/26 6:09 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityVO extends Commodity {

    @ApiModelProperty(value = "SKU库存")
    private Integer quantity;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;
}
