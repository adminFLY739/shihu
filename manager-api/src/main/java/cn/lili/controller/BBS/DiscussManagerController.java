package cn.lili.controller.BBS;

import cn.lili.common.aop.annotation.DemoSite;
import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.discuss.entity.vo.DiscussVO;
import cn.lili.modules.discuss.service.DiscussManagerService;
import cn.lili.modules.post.entity.vo.PostVO;
import cn.lili.modules.post.service.PostManagerService;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.robot.entity.dto.ManagerRobotEditDTO;
import cn.lili.modules.robot.entity.dto.RobotAddDTO;
import cn.lili.modules.robot.entity.vo.RobotSearchVO;
import cn.lili.modules.robot.entity.vo.RobotVO;
import cn.lili.modules.robot.service.RobotService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "管理端")
@RequestMapping("/manager/discusses")
public class DiscussManagerController {
    @Autowired
    private DiscussManagerService postManagerService;

    @ApiOperation(value = "分页列表")
    @GetMapping(value = "/list")
    public ResultMessage<IPage<DiscussVO>> getByPage(PageVO page) {
        return ResultUtil.data(postManagerService.getMemberPage(page));
    }

    @ApiOperation(value = "删除机器人")
    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable String id) {
        postManagerService.deleteRobotById(id);
    }
}
