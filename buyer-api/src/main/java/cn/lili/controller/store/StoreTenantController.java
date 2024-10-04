package cn.lili.controller.store;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.modules.store.entity.vos.StoreSearchParams;
import cn.lili.modules.store.entity.vos.StoreVO;
import cn.lili.modules.store.service.StoreService;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/15 17:04
 * @description: 店铺租户接口
 */


@RestController
@Api(tags = "店铺端,店铺租户接口 ")
@RequestMapping("/buyer/store/tenant")
public class StoreTenantController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreTenantService storeTenantService;


    @ApiOperation(value = "获取店铺租户")
    @GetMapping
    public ResultMessage<List<Tenant>> getTenantList() {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null ) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        return ResultUtil.data(memberService.getMemberTenantList(tokenUser.getId()));
    }

    @ApiOperation(value = "获取当前用户店铺分页列表")
    @GetMapping("/userStore")
    public ResultMessage<IPage<StoreVO>> getByPage(StoreSearchParams entity, PageVO page) {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null ) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        entity.setMemberId(tokenUser.getId());
        return ResultUtil.data(storeService.findByConditionPage(entity, page));
    }


    @ApiOperation(value = "取消申请")
    @PutMapping("/cancel")
    public ResultMessage<Object> cancelApply(String storeId,String tenantId) {
        if(storeTenantService.ChangeStoreDisable(storeId,tenantId, StoreStatusEnum.APPLY.value())){
            return ResultUtil.success();
        }else {
            return ResultUtil.error(ResultCode.UPDATE_STORE_STATUS_ERROR);
        }

    }

}
