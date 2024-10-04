package cn.lili.modules.member.entity.vo;

import cn.lili.modules.member.entity.dos.MemberTenant;
import cn.lili.modules.tenant.entity.dos.Tenant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/7/12 09:10
 * @description: 主租户VO
 */

@Data
public class MainTenantVO {

    @ApiModelProperty(value = "租户id")
    private String tenantId;


    public MainTenantVO(MemberTenant memberTenant){
        this.tenantId=memberTenant.getTenantId();
    }

    public MainTenantVO(Tenant tenant){
       this.tenantId=tenant.getId();
    }

}
