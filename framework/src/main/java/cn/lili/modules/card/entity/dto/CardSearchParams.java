package cn.lili.modules.card.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.modules.promotion.entity.enums.PromotionsScopeTypeEnum;
import cn.lili.modules.promotion.entity.enums.PromotionsStatusEnum;
import cn.lili.modules.promotion.tools.PromotionTools;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/17 14:03
 * @description: 卡券搜索参数
 */

@Data
public class CardSearchParams {

    private static final String PRICE_COLUMN = "price";

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

    /**
     * @see PromotionsScopeTypeEnum
     */
    @ApiModelProperty(value = "关联范围类型")
    private String scopeType;

    @ApiModelProperty(value = "店铺编号 如有多个','分割")
    private String storeId;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "卡券名称")
    private String cardName;

    @ApiModelProperty(value = "范围关联的id")
    private String scopeId;

    @ApiModelProperty(value = "发行数量,可以为范围，如10_1000")
    private String publishNum;
    @ApiModelProperty(value = "已被领取的数量,可以为范围，如10_1000")
    private String receivedNum;

    @ApiModelProperty(value = "面额,可以为范围，如10_1000")
    private String price;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = this.baseQueryWrapper();

        if (CharSequenceUtil.isNotEmpty(promotionStatus)) {
            queryWrapper.and(i -> {
                for (String status : promotionStatus.split(",")) {
                    i.or(PromotionTools.queryPromotionStatus(PromotionsStatusEnum.valueOf(status)));
                }
            });
        }
        return queryWrapper;
    }

    public <T> QueryWrapper<T> baseQueryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (CharSequenceUtil.isNotEmpty(cardName)) {
            queryWrapper.like("card_name", cardName);
        }
        if (startTime != null) {
            queryWrapper.ge("start_time", new Date(startTime));
        }
        if (endTime != null) {
            queryWrapper.le("end_time", new Date(endTime));
        }
        if (CharSequenceUtil.isNotEmpty(scopeType)) {
            queryWrapper.eq("scope_type", scopeType);
        }
        if (CharSequenceUtil.isNotEmpty(storeId)) {
            queryWrapper.in("me.store_id", Arrays.asList(storeId.split(",")));
        }
        if (CharSequenceUtil.isNotEmpty(scopeId)) {
            queryWrapper.eq("scope_id", scopeId);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        if (CharSequenceUtil.isNotEmpty(this.getPromotionStatus())) {
            queryWrapper.and(p -> {
                switch (PromotionsStatusEnum.valueOf(this.getPromotionStatus())) {
                    case NEW:
                        p.nested(i -> i.gt(PromotionTools.START_TIME_COLUMN, new Date()).gt(PromotionTools.END_TIME_COLUMN, new Date()));
                        break;
                    case START:
                        p.nested(i -> i.le(PromotionTools.START_TIME_COLUMN, new Date()).ge(PromotionTools.END_TIME_COLUMN, new Date()));
                        break;
                    case END:
                        p.nested(i -> i.lt(PromotionTools.START_TIME_COLUMN, new Date()).lt(PromotionTools.END_TIME_COLUMN, new Date()));
                        break;
                    case CLOSE:
                        p.nested(n -> n.nested(i -> i.isNull(PromotionTools.START_TIME_COLUMN).isNull(PromotionTools.END_TIME_COLUMN)));
                        break;
                    default:
                }
            });

        }
        if (this.getStartTime() != null) {
            queryWrapper.ge("start_time", new Date(this.getEndTime()));
        }
        if (this.getEndTime() != null) {
            queryWrapper.le("end_time", new Date(this.getEndTime()));
        }
        this.betweenWrapper(queryWrapper);
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }
    private <T> void betweenWrapper(QueryWrapper<T> queryWrapper) {
        if (CharSequenceUtil.isNotEmpty(publishNum)) {
            String[] s = publishNum.split("_");
            if (s.length > 1) {
                queryWrapper.between("publish_num", s[0], s[1]);
            } else {
                queryWrapper.ge("publish_num", s[0]);
            }
        }
        if (CharSequenceUtil.isNotEmpty(price)) {
            String[] s = price.split("_");
            if (s.length > 1) {
                queryWrapper.between(PRICE_COLUMN, s[0], s[1]);
            } else {
                queryWrapper.ge(PRICE_COLUMN, s[0]);
            }
        }
        if (CharSequenceUtil.isNotEmpty(receivedNum)) {
            String[] s = receivedNum.split("_");
            if (s.length > 1) {
                queryWrapper.between("received_num", s[0], s[1]);
            } else {
                queryWrapper.ge("received_num", s[0]);
            }
        }
    }
}
