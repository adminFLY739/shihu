package cn.lili.controller.member;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.entity.dto.MemberTenantSearchParams;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import cn.lili.modules.member.service.MemberTenantService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/7/4 13:42
 * @description: 管理端，用户租户管理
 */

@RestController
@Api(tags = "管理端,用户租户管理接口")
@RequestMapping("/manager/member/memberTenant")
public class MemberTenantManagerController {


    @Autowired
    private MemberTenantService memberTenantService;


    @ApiOperation(value = "获取当前用户租户分页列表")
    @GetMapping()
    public ResultMessage<IPage<MemberTenantVO>> getByPage(MemberTenantSearchParams entity, PageVO page) {
        return ResultUtil.data(memberTenantService.getByPage(entity,page));
    }

    @ApiOperation(value = "修改用户租户状态")
    @PutMapping("/updateStatus")
    public ResultMessage<Object> updateMemberTenantStatus(@RequestParam String memberId, @RequestParam String tenantId , @RequestParam String memberStatus) {
        memberTenantService.updateMemberTenantStatus(memberId, tenantId,memberStatus);
        return ResultUtil.success();
    }

}
