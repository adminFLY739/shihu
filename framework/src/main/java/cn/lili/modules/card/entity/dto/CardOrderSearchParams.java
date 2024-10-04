package cn.lili.modules.card.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.utils.DateUtil;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/27 16:47
 * @description: 卡券订单搜索参数
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardOrderSearchParams {

    @ApiModelProperty(value = "卡券名称")
    private String cardName;

    @ApiModelProperty(value = "卡券订单号")
    private String sn;

    @ApiModelProperty(value = "用户名")
    private String memberName;

    @ApiModelProperty(value = "提货码")
    private String deliveryCode;

    @ApiModelProperty(value = "店铺id")
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;


    /**
     * @see OrderStatusEnum
     */
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "下单开始时间")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "下单结束时间")
    private Date endDate;

    public <T> QueryWrapper<T> queryWrapper() {

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(cardName)) {
            queryWrapper.like("card_name", cardName);
        }
        if (CharSequenceUtil.isNotEmpty(sn)) {
            queryWrapper.like("sn", sn);
        }
        //按时间查询
        queryWrapper.ge(startDate != null, "create_time", startDate);

        queryWrapper.le(endDate != null, "create_time", DateUtil.endOfDate(endDate));
        if (CharSequenceUtil.isNotEmpty(orderStatus)) {
            queryWrapper.eq("order_status", orderStatus);
        }
        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(deliveryCode)) {
            queryWrapper.like("delivery_code", deliveryCode);
        }
        if (CharSequenceUtil.isNotEmpty(storeId)) {
            queryWrapper.eq("store_id", storeId);
        }
        if (CharSequenceUtil.isNotEmpty(storeName)) {
            queryWrapper.like("store_name", storeName);
        }

        return queryWrapper;
    }



}
