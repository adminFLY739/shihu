package cn.lili.modules.store.entity.dos;

import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 角色权限绑定关系
 *
 * @author nxc
 * @since 2023/6/15 13:19
 */
@Data
@TableName("li_store_tenant")
@ApiModel(value = "店铺租户关系")
public class StoreTenant extends BaseEntity {

    private static final long serialVersionUID = -4680260092546996026L;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * @see StoreStatusEnum
     */
    @ApiModelProperty(value = "店铺状态")
    private String storeDisable;

    @ApiModelProperty(value = "店铺id")
    private String storeId;


}
