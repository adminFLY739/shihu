package cn.lili.modules.card.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: nxc
 * @since: 2023/6/19 11:22
 * @description: 添加提货码DTO
 */

@Data
@NoArgsConstructor
public class DeliveryImportDTO {

    @ApiModelProperty(value = "卡券id")
    String cardId;

    @ApiModelProperty(value = "添加数量")
    int publishNum;
}
