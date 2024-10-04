package cn.lili.modules.card.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/27 09:43
 * @description: 卡券订单
 */


@Data
@TableName("li_card_order")
@ApiModel(value = "卡券订单")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CardOrder extends BaseEntity {
    private static final long serialVersionUID = -417977317840577947L;

    @ApiModelProperty(value = "商家ID")
    private String storeId;

    @ApiModelProperty(value = "商家名称")
    private String storeName;

    @ApiModelProperty(value = "卡券取消原因")
    private String cancel_reason;

    @ApiModelProperty(value = "完成时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    @ApiModelProperty(value = "会员ID")
    public String memberId;

    @ApiModelProperty(value = "会员名称")
    public String memberName;

    @ApiModelProperty(value = "订单价格")
    private Double price;

    @ApiModelProperty(value = "订单编号")
    public String sn;

    @ApiModelProperty(value = "卡券ID")
    public String cardId;

    @ApiModelProperty(value = "卡券名称")
    public String cardName;

    @ApiModelProperty(value = "提货码")
    public String deliveryCode;

    /**
     * @see cn.lili.modules.order.order.entity.enums.PayStatusEnum
     */
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "第三方付款流水号")
    private String receivableNo;


    public CardOrder(Card card){
        this.cardId = card.getId();
        this.price = card.getPrice();
        this.storeId=card.getStoreId();
        this.storeName= card.getStoreName();
        this.cardName = card.getCardName();
    }




}
