package cn.lili.modules.card.entity.dos;

import cn.lili.modules.promotion.entity.enums.PromotionsStatusEnum;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/6/17 09:38
 * @description: 卡券实体类
 */

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("li_card")
@ApiModel(value = "卡券")
public class Card extends BaseEntity {
    private static final long serialVersionUID = 5568491576922052426L;

    @ApiModelProperty(value = "卡券名称")
    private String cardName;

    @ApiModelProperty(value = "商家名称")
    private String storeName;

    @ApiModelProperty(value = "商家id")
    private String storeId;

    @NotEmpty(message = "活动名称不能为空")
    @ApiModelProperty(value = "活动名称", required = true)
    private String promotionName;

    @ApiModelProperty(value = "活动开始时间", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss|| yyyy/MM/dd ||epoch_millis")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss|| yyyy/MM/dd ||epoch_millis")
    private Date endTime;


    @ApiModelProperty(value = "范围关联的id")
    private String scopeId;

    @ApiModelProperty(value = "活动描述")
    private String description;

    @ApiModelProperty(value = "面额")
    private Double price;

    @ApiModelProperty(value = "已被使用的数量")
    private Integer usedNum;

    @ApiModelProperty(value = "已被领取的数量")
    private Integer receivedNum;

    @ApiModelProperty(value = "发行数量")
    private Integer publishNum;

    @ApiModelProperty(value = "卡券图片")
    private String image;



    public String getPromotionStatus() {
        if (endTime == null) {
            return startTime != null ? PromotionsStatusEnum.START.name() : PromotionsStatusEnum.CLOSE.name();
        }
        Date now = new Date();
        if (now.before(startTime)) {
            return PromotionsStatusEnum.NEW.name();
        } else if (endTime.before(now)) {
            return PromotionsStatusEnum.END.name();
        } else if (now.before(endTime)) {
            return PromotionsStatusEnum.START.name();
        }
        return PromotionsStatusEnum.CLOSE.name();
    }

}
