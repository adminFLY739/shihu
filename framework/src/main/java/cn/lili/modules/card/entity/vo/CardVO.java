package cn.lili.modules.card.entity.vo;

import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.CardGoods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 13:59
 * @description: 卡券VO
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "优惠券")
@ToString(callSuper = true)
@NoArgsConstructor
public class CardVO extends Card {

    private static final long serialVersionUID = -2952989197930876517L;
    /**
     * 促销关联的商品
     */
    @ApiModelProperty(value = "优惠券关联商品集合")
    private List<CardGoods> cardGoodsList;

    @ApiModelProperty(value = "剩余数量")
    private long overNum;




    public CardVO(Card card) {
        if (card == null) {
            return;
        }
        BeanUtils.copyProperties(card, this);
    }
}
