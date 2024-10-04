package cn.lili.controller.passport;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/25 16:17
 * @description: 用户端用户租户接口
 */

@RestController
@Api(tags = "用户端,用户租户接口 ")
@RequestMapping("/store/store/tenant")
public class StoreTenantController {

    @Autowired
    private StoreTenantService storeTenantService;

    @ApiOperation(value = "获取店铺租户")
    @GetMapping
    public ResultMessage<List<Tenant>> getMemberTenantList() {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null || CharSequenceUtil.isEmpty(tokenUser.getStoreId())) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        return ResultUtil.data(storeTenantService.getTenantListByStoreId(tokenUser.getStoreId()));
    }
}
