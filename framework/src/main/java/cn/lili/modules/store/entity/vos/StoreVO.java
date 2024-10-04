package cn.lili.modules.store.entity.vos;

import cn.lili.modules.store.entity.dos.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店铺VO
 *
 * @author pikachu
 * @since 2020-03-07 17:02:05
 */
@Data
public class StoreVO extends Store {

    private static final long serialVersionUID = 925682455089542399L;
    @ApiModelProperty(value = "库存预警数量")
    private Integer stockWarning;

    @ApiModelProperty(value = "登录用户的昵称")
    private String nickName;

    @ApiModelProperty(value = "店铺状态")
    private String storeDisable;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;


    @ApiModelProperty(value = "租户Id")
    private String tenantId;



}
