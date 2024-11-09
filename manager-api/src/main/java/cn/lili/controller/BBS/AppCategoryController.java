package cn.lili.controller.BBS;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.entity.CategoryEntity;
import cn.lili.modules.BBS.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Api(tags = "用户端——分类")
@RestController
@RequestMapping("manager/bbs/topic")
public class AppCategoryController {


    @Autowired
    private CategoryService categoryService;


    @GetMapping("/classList")
    @ApiOperation("分类列表")
    public R classList(){
        List<CategoryEntity> list = categoryService.list();
        System.out.println("listlistlist" + list);
        return R.ok().put("result",list);
    }
}
