package cn.lili.modules.card.entity.vo;

import cn.lili.modules.card.entity.dos.Delivery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: nxc
 * @since: 2023/6/19 10:09
 * @description: 提货码VO
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "提货码VO")
@ToString(callSuper = true)
@NoArgsConstructor
public class DeliveryVO extends Delivery {
    private static final long serialVersionUID = -3455144750570040365L;

    @ApiModelProperty(value = "卡券名称")
    private String cardName;

    @ApiModelProperty(value = "卡券名称")
    private String image;

}
