package cn.lili.controller.member;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.entity.dto.FootPrintQueryParams;
import cn.lili.modules.member.service.FootprintService;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


/**
 * 买家端,浏览历史接口
 *
 * @author Chopper
 * @since 2020/11/16 10:06 下午
 */
@RestController
@Api(tags = "买家端,浏览历史接口")
@RequestMapping("/buyer/member/footprint")
public class FootprintController {

    /**
     * 会员足迹
     */
    @Autowired
    private FootprintService footprintService;

    @ApiOperation(value = "分页获取")
    @GetMapping
    public ResultMessage<IPage<EsGoodsIndex>> getByPage(FootPrintQueryParams params) {
        params.setMemberId(Objects.requireNonNull(UserContext.getCurrentUser()).getId());
        return ResultUtil.data(footprintService.footPrintPage(params));
    }

    @ApiOperation(value = "根据id删除")
    @ApiImplicitParam(name = "ids", value = "商品ID", required = true, allowMultiple = true, dataType = "String", paramType = "path")
    @DeleteMapping(value = "/delByIds/{ids}")
    public ResultMessage<Object> delAllByIds(@NotNull(message = "商品ID不能为空") @PathVariable("ids") List<String> ids) {
        footprintService.deleteByIds(ids);
        return ResultUtil.success();

    }

    @ApiOperation(value = "清空足迹")
    @DeleteMapping
    public ResultMessage<Object> deleteAll() {
        footprintService.clean();
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取当前会员足迹数量")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, paramType = "query")
    @GetMapping(value = "/getFootprintNum")
    public ResultMessage<Object> getFootprintNum(String tenantId) {
        return ResultUtil.data(footprintService.getFootprintNum(tenantId));
    }


    @GetMapping("/history")
    @ApiOperation(value = "获取会员的历史足迹")
    public ResultMessage<IPage<EsGoodsIndex>> getMemberHistory(FootPrintQueryParams params) {
        return ResultUtil.data(footprintService.footPrintPage(params));
    }
}
