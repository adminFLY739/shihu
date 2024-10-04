package cn.lili.modules.card.entity.dos;

import cn.lili.modules.card.entity.enums.deliveryStatus;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/6/19 08:59
 * @description: 提货码实体类
 */

@Data
@TableName("li_delivery")
@ApiModel(value = "提货码")
public class Delivery extends BaseEntity {

    private static final long serialVersionUID = -3527187068454700716L;

    @ApiModelProperty(value = "卡券id")
    private String cardId;

    @ApiModelProperty(value = "提货码")
    private String deliveryCode;

    @ApiModelProperty(value = "提货密码")
    private String deliveryPassword;


    /**
     * @see deliveryStatus
     */
    @ApiModelProperty(value = "提货状态")
    private String deliveryStatus;

    @ApiModelProperty(value = "会员ID")
    public String memberId;
}
