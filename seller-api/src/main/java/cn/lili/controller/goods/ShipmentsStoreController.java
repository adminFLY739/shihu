package cn.lili.controller.goods;


import cn.lili.common.enums.ResultUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.goods.entity.dos.GoodsUnit;
import cn.lili.modules.goods.entity.dos.Shipments;
import cn.lili.modules.goods.service.ShipmentsService;
import cn.lili.modules.member.entity.vo.ClerkVO;
import cn.lili.modules.member.service.ShipmentsClerkService;
import cn.lili.modules.tenant.entity.dos.Tenant;
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
import java.util.Objects;

/**
 * 店铺端,商品发货地接口
 *
 * @author Bulbasaur
 * @since 2020/11/26 16:15
 */
@RestController
@Api(tags = "店铺端,商品发货地接口")
@RequestMapping("/store/goods/shipments")
public class ShipmentsStoreController {

    @Autowired
    private ShipmentsService shipmentsService;

    @Autowired
    private ShipmentsClerkService shipmentsClerkService;

    @ApiOperation(value = "分页获取商品发货地")
    @GetMapping
    public ResultMessage<IPage<Shipments>> getByPage(PageVO pageVO) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        return ResultUtil.data(shipmentsService.queryByParams(pageVO,storeId));
    }

    @ApiOperation(value = "添加商品发货地")
    @PostMapping
    public ResultMessage<Shipments> save(Shipments shipments) {
        String storeId = Objects.requireNonNull(UserContext.getCurrentUser()).getStoreId();
        shipments.setStoreId(storeId);
        shipmentsService.save(shipments);
        return ResultUtil.data(shipments);
    }

    @ApiOperation(value = "编辑商品发货地")
    @ApiImplicitParam(name = "id", value = "商品发货地ID", required = true, paramType = "path")
    @PutMapping("/{id}")
    public ResultMessage<Shipments> update(@NotNull @PathVariable String id, @Valid Shipments shipments) {
      shipments.setId(id);
      shipmentsService.updateById(shipments);
      return ResultUtil.data(shipments);
    }

    @ApiOperation(value = "删除发货地")
    @ApiImplicitParam(name = "ids", value = "发货地ID", required = true, paramType = "path")
    @DeleteMapping("/{ids}")
    public ResultMessage<Object> delete(@NotNull @PathVariable List<String> ids) {
      shipmentsService.removeByIds(ids);
      return ResultUtil.success();
    }

  @ApiOperation(value = "添加商品发货地店员")
  @PostMapping("/shipmentsClerk")
  public ResultMessage<Object> save(@RequestParam List<String> clerkIds,String shipmentsId) {
    shipmentsClerkService.addShipmentsClerk(clerkIds,shipmentsId);
    return ResultUtil.success();
  }

  @ApiOperation(value = "获取商品发货地店员")
  @GetMapping("/getShipmentsClerk")
  public ResultMessage<List<ClerkVO>> save(String shipmentsId) {
    return ResultUtil.data(shipmentsClerkService.getShipmentsClerk(shipmentsId));
  }
}
