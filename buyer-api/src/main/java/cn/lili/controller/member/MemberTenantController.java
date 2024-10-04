package cn.lili.controller.member;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.member.entity.dos.MemberTenant;
import cn.lili.modules.member.entity.dto.MemberTenantSearchParams;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import cn.lili.modules.member.service.MemberTenantService;
import cn.lili.modules.store.entity.vos.StoreSearchParams;
import cn.lili.modules.store.entity.vos.StoreVO;
import cn.lili.modules.tenant.service.TenantAreaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: nxc
 * @since: 2023/7/4 09:34
 * @description: 用户租户控制层
 */

@RestController
@Api(tags = "买家端,用户租户接口")
@RequestMapping("/buyer/member/tenant")
public class MemberTenantController {

    @Autowired
    private MemberTenantService memberTenantService;

    @Autowired
    private TenantAreaService tenantAreaService;

    @ApiOperation(value = "获取当前用户租户分页列表")
    @GetMapping()
    public ResultMessage<IPage<MemberTenantVO>> getByPage(MemberTenantSearchParams entity, PageVO page) {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null ) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        entity.setMemberId(tokenUser.getId());
        return ResultUtil.data(memberTenantService.getByPage(entity,page));
    }


    @ApiOperation(value = "申请租户")
    @ApiImplicitParam(name = "tenantIds", value = "租户ids字符串", required = true , paramType = "query")
    @PutMapping("/apply")
    public ResultMessage<Object> applyTenantId(@RequestParam String tenantIds) {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        List<String> tenantIdList = Arrays.asList(tenantIds.split(","));
        if (memberTenantService.applyTenantId(tenantIdList,currentUser.getId())) {
            return ResultUtil.success(ResultCode.APPLY_MEMBER_TENANT_SUCCESS);
        }
        throw new ServiceException(ResultCode.APPLY_MEMBER_TENANT_ERROR);
    }


    @ApiOperation(value = "获取所有租户")
    @GetMapping("/allTenant")
    public ResultMessage<Object> getAllTenant(){
        return ResultUtil.data(tenantAreaService.list());
    }

    @ApiOperation(value = "修改用户租户状态")
    @ApiImplicitParams({
      @ApiImplicitParam(name = "memberId", value = "用户id" , required = true , paramType = "query"),
      @ApiImplicitParam(name = "tenantId", value = "租户id" , required = true , paramType = "query")
    })
    @PutMapping("/updateStatus")
    public ResultMessage<Object> updateMemberTenantStatus(@RequestParam String memberId, @RequestParam String tenantId , @RequestParam String memberStatus) {
        memberTenantService.updateMemberTenantStatus(memberId, tenantId,memberStatus);
        return ResultUtil.success();
    }


    @ApiOperation(value = "设置主租户")
    @ApiImplicitParam(name = "tenantId" , value = "租户id" , required = true , paramType = "query")
    @PutMapping("/setMain")
    public ResultMessage<Object> setMainTenant(@RequestParam String tenantId) {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        if (memberTenantService.setMainTenant(tenantId,currentUser.getId())) {
            return ResultUtil.success(ResultCode.SET_MAIN_TENANT_SUCCESS);
        }
        throw new ServiceException(ResultCode.SET_MAIN_TENANT_ERROR);
    }
    @ApiOperation(value = "获得当前小程序租户")
    @ApiImplicitParam(name = "appid" , value = "小程序appid" , required = true , paramType = "query")
    @GetMapping("/MainTenant")
    public ResultMessage<Object> getMainTenant(String appid) {
      return ResultUtil.data(memberTenantService.getMainTenant(appid));
    }

}
