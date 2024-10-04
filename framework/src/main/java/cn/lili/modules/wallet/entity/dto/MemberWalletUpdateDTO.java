package cn.lili.modules.wallet.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 会员余额变动模型
 *
 * @author Chopper
 * @version v1.0
 * 2021-12-01 09:35
 */
@Data
@AllArgsConstructor
public class MemberWalletUpdateDTO {

    @ApiModelProperty(value = "变动金额")
    private Double money;
    @ApiModelProperty(value = "变动会员id")
    private String memberId;
    @ApiModelProperty(value = "日志详情")
    private String detail;

    @ApiModelProperty(value = "变动租户id")
    private String tenantId;

    /**
     * @see cn.lili.modules.wallet.entity.enums.DepositServiceTypeEnum
     */
    @ApiModelProperty(value = "变动业务原因")
    private String serviceType;


   public MemberWalletUpdateDTO(Double money,String memberId,String detail,String serviceType ){
       this.money=money;
       this.memberId=memberId;
       this.detail=detail;
       this.serviceType=serviceType;
    }
}
