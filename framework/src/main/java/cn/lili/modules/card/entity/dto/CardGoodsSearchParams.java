package cn.lili.modules.card.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.modules.promotion.entity.enums.PromotionsStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 15:18
 * @description: 卡券商品查询
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardGoodsSearchParams {
    @ApiModelProperty(value = "卡券id")
    private String cardId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品分类路径")
    private String categoryPath;

    @ApiModelProperty(value = "商品SkuId")
    private String skuId;

    @ApiModelProperty(value = "商品SkuIds")
    private List<String> skuIds;

    @ApiModelProperty(value = "卡券ids")
    private List<String> cardIds;

    @ApiModelProperty(value = "活动id")
    private String id;

    @ApiModelProperty(value = "活动开始时间")
    private Long startTime;

    @ApiModelProperty(value = "活动结束时间")
    private Long endTime;

    /**
     * @see PromotionsStatusEnum
     */
    @ApiModelProperty(value = "活动状态 如需同时判断多个活动状态','分割")
    private String promotionStatus;


    @ApiModelProperty(value = "店铺编号 如有多个','分割")
    private String storeId;



    public <T> QueryWrapper<T> queryWrapper() {

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(cardId)) {
            queryWrapper.eq("card_id", cardId);
        }
        if (CharSequenceUtil.isNotEmpty(goodsName)) {
            queryWrapper.like("goods_name", goodsName);
        }
        if (CharSequenceUtil.isNotEmpty(categoryPath)) {
            queryWrapper.like("category_path", categoryPath);
        }
        if (CharSequenceUtil.isNotEmpty(skuId)) {
            queryWrapper.in("sku_id", Arrays.asList(skuId.split(",")));
        }
        if (skuIds != null && !skuIds.isEmpty()) {
            queryWrapper.in("sku_id", skuIds);
        }
        if (cardIds != null && cardIds.isEmpty()) {
            queryWrapper.in("card_id", cardIds);
        }
        if (CharSequenceUtil.isNotEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (startTime != null) {
            queryWrapper.ge("start_time", new Date(startTime));
        }
        if (endTime != null) {
            queryWrapper.le("end_time", new Date(endTime));
        }
        if (CharSequenceUtil.isNotEmpty(storeId)) {
            queryWrapper.in("me.store_id", Arrays.asList(storeId.split(",")));
        }
        return queryWrapper;
    }


}
