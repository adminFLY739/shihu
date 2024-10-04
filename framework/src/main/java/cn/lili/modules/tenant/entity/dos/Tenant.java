package cn.lili.modules.tenant.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author: nxc
 * @since: 2023/6/12 09:52
 * @description: 租户区域实体类
 */

@Data
@TableName("tenant")
@ApiModel(value = "租户区域")
public class Tenant extends BaseEntity {

    @NotEmpty(message = "租户区域名称不能为空")
    @ApiModelProperty(value = "租户区域名称")
    private String name;


    @ApiModelProperty(value = "租户小程序id")
    private String appid;

}
