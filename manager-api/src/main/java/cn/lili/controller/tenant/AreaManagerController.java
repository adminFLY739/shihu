package cn.lili.controller.tenant;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/12 09:48
 * @description: 租户区域管理控制层
 */

@RestController
@Api(tags = "管理端,租户区域接口")
@RequestMapping("/manager/tenant/area")
public class AreaManagerController {


    @Autowired
    private TenantAreaService tenantAreaService;


    @ApiOperation(value = "分页获取租户区域")
    @GetMapping
    public ResultMessage<IPage<Tenant>> getByPage(PageVO pageVO) {
        return ResultUtil.data(tenantAreaService.page(PageUtil.initPage(pageVO)));
    }

    @ApiOperation(value = "根据ID获取租户区域")
    @ApiImplicitParam(name = "id", value = "租户区域ID", required = true, paramType = "path")
    @GetMapping("/get/{id}")
    public ResultMessage<Tenant> getById(@NotNull @PathVariable String id) {
        return ResultUtil.data(tenantAreaService.getById(id));
    }

    @ApiOperation(value = "添加租户区域")
    @PostMapping
    public ResultMessage<Tenant> save(@Valid Tenant tenant) {
        tenantAreaService.saveTenant(tenant);

        return ResultUtil.data(tenant);
    }

    @ApiOperation(value = "编辑租户区域")
    @ApiImplicitParam(name = "id", value = "租户区域ID", required = true, paramType = "path")
    @PutMapping("/{id}")
    public ResultMessage<Tenant> update(@NotNull @PathVariable String id, @Valid Tenant tenant) {
        tenant.setId(id);
        tenantAreaService.updateById(tenant);
        return ResultUtil.data(tenant);
    }

    @ApiOperation(value = "删除租户区域")
    @ApiImplicitParam(name = "ids", value = "租户区域ID", required = true, paramType = "path")
    @DeleteMapping("/delete/{ids}")
    public ResultMessage<Object> delete(@NotNull @PathVariable List<String> ids) {
        tenantAreaService.removeByIds(ids);
        return ResultUtil.success();
    }

}
