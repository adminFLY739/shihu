package cn.lili.controller.store;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.entity.vo.StoreMenuVO;
import cn.lili.modules.member.service.StoreMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 管理端,菜单管理接口
 *
 * @author nxc
 * @since 2020/11/20 12:07
 */
@Slf4j
@RestController
@Api(tags = "店铺端,菜单管理接口")
@RequestMapping("/buyer/store/menu")
public class StoreMenuController {

    @Autowired
    private StoreMenuService storeMenuService;


    @ApiOperation(value = "获取所有菜单")
    @GetMapping("/tree")
    public ResultMessage<List<StoreMenuVO>> getAllMenuList() {
        return ResultUtil.data(storeMenuService.tree());
    }
    // public ResultMessage<List<StoreMenuVO>> getAllMenuList(String tenantId) {
    //     return ResultUtil.data(storeMenuService.tree(tenantId));
    // }

    @ApiOperation(value = "获取所有菜单---根据当前用户角色")
    @GetMapping("/memberMenu")
    public ResultMessage<List<StoreMenuVO>> memberMenu() {
        return ResultUtil.data(storeMenuService.findUserTree());
    }
}
