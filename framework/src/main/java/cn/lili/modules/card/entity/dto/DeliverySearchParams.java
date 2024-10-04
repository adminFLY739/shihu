package cn.lili.modules.card.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;


/**
 * @author: nxc
 * @since: 2023/6/19 10:07
 * @description: 提货码搜索参数
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverySearchParams {

    @ApiModelProperty(value = "卡券id")
    private String cardId;

    @ApiModelProperty(value = "卡券名称")
    private String cardName;

    /**
     * @see cn.lili.modules.card.entity.enums.deliveryStatus
     */

    @ApiModelProperty(value = "提货码状态")
    private String deliveryStatus;

    @ApiModelProperty(value = "店铺编号 如有多个','分割")
    private String storeId;

    @ApiModelProperty(value = "用户id")
    private String memberId;

    public <T> QueryWrapper<T> queryWrapper() {

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(cardId)) {
            queryWrapper.eq("me.card_id", cardId);
        }
        if (CharSequenceUtil.isNotEmpty(cardName)) {
            queryWrapper.like("card_name", cardName);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("member_id", memberId);
        }
        if (CharSequenceUtil.isNotEmpty(deliveryStatus)) {
            queryWrapper.like("delivery_status", deliveryStatus);
        }

        if (CharSequenceUtil.isNotEmpty(storeId)) {
            queryWrapper.in("store_id", Arrays.asList(storeId.split(",")));
        }
        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }

}
