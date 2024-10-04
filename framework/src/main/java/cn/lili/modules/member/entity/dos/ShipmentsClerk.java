package cn.lili.modules.member.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author: nxc
 * @since: 2023/7/4 09:25
 * @description: 发货地店员管理
 */

@Data
@TableName("li_shipments_clerk")
@ApiModel(value = "发货地店员关系")
public class ShipmentsClerk extends BaseEntity {

    private static final long serialVersionUID = 4317475923479949882L;
    @ApiModelProperty(value = "发货地id")
    private String shipmentsId;

    @ApiModelProperty(value = "店员id")
    private String clerkId;

}
