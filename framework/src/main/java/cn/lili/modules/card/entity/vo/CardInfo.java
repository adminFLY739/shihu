package cn.lili.modules.card.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @author: nxc
 * @since: 2023/6/20 13:52
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "优惠券")
@ToString(callSuper = true)
@NoArgsConstructor
public class CardInfo extends CardVO {

    private static final long serialVersionUID = 594106876486418284L;

    /**
     * @see cn.lili.modules.card.entity.enums.deliveryStatus
     */
    @ApiModelProperty(value = "提货状态")
    private String deliveryStatus;

    @ApiModelProperty(value = "提货码id")
    private String deliveryId;


    public CardInfo(CardVO card) {
        if (card == null) {
            return;
        }
        BeanUtils.copyProperties(card, this);
    }
}
