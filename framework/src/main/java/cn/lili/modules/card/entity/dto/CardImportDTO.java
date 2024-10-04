package cn.lili.modules.card.entity.dto;

import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.CardGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 10:01
 * @description: 卡券生成DTO
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CardImportDTO extends Card {

    private static final long serialVersionUID = -8212863852510140193L;
    /**
     * 促销关联的商品
     */
    @ApiModelProperty(value = "卡券关联商品集合")
    private List<CardGoods> cardGoodsList;

    public CardImportDTO(Card card) {
        if (card == null) {
            return;
        }
        BeanUtils.copyProperties( card, this);
    }



}
