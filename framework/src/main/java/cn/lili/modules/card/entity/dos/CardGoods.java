package cn.lili.modules.card.entity.dos;

import cn.hutool.core.bean.BeanUtil;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.promotion.entity.enums.PromotionsScopeTypeEnum;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/17 10:39
 * @description: 卡券商品实体类
 */

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("li_card_goods")
@ApiModel(value = "卡券商品")
@NoArgsConstructor
public class CardGoods  extends BaseEntity {
    private static final long serialVersionUID = 4150737500248136108L;

    @ApiModelProperty(value = "商家ID")
    private String storeId;

    @ApiModelProperty(value = "商家名称")
    private String storeName;

    @ApiModelProperty(value = "商品id")
    private String goodsId;

    @ApiModelProperty(value = "商品SkuId")
    private String skuId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "缩略图")
    private String thumbnail;

    @ApiModelProperty(value = "活动开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss|| yyyy/MM/dd ||epoch_millis")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss|| yyyy/MM/dd ||epoch_millis")
    private Date endTime;

    @ApiModelProperty(value = "卡券id")
    private String cardId;


    /**
     * @see cn.lili.modules.goods.entity.enums.GoodsTypeEnum
     */
    @ApiModelProperty(value = "商品类型")
    private String goodsType;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "卖出的商品数量")
    private Integer num;

    @ApiModelProperty(value = "原价")
    private Double originalPrice;

    @ApiModelProperty(value = "促销价格")
    private Double price;

    @ApiModelProperty(value = "兑换积分")
    private Long points;

    @ApiModelProperty(value = "限购数量")
    private Integer limitNum;

    @ApiModelProperty(value = "促销库存")
    private Integer quantity;

    @ApiModelProperty(value = "分类path")
    private String categoryPath;

    /**
     * @see PromotionsScopeTypeEnum
     */
    @ApiModelProperty(value = "关联范围类型")
    private String scopeType = PromotionsScopeTypeEnum.PORTION_GOODS.name();


    @ApiModelProperty(value = "范围关联的id")
    private String scopeId;

    public CardGoods(GoodsSku sku) {
        if (sku != null) {
            BeanUtil.copyProperties(sku, this, "id", "price");
            this.skuId = sku.getId();
            this.originalPrice = sku.getPrice();
        }
    }

}
