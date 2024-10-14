package cn.lili.controller.passport;

import cn.lili.common.aop.annotation.DemoSite;
import cn.lili.common.aop.annotation.PreventDuplicateSubmissions;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
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
@Api(tags = "管理端,机器人接口")
@RequestMapping("/manager/passport/robot")
public class RobotManagerController {
    @Autowired
    private RobotService robotService;

    @ApiOperation(value = "机器人分页列表")
    @GetMapping
    public ResultMessage<IPage<RobotVO>> getByPage(RobotSearchVO memberSearchVO, PageVO page) {
        return ResultUtil.data(robotService.getMemberPage(memberSearchVO, page));
    }


    @ApiOperation(value = "通过ID获取会员信息")
    @ApiImplicitParam(name = "id", value = "会员ID", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/{id}")
    public ResultMessage<RobotVO> get(@PathVariable String id) {
        return ResultUtil.data(robotService.getMember(id));
    }

    @ApiOperation(value = "添加会员")
    @PostMapping
    public ResultMessage<Robot> save(@Valid RobotAddDTO member) {
        return ResultUtil.data(robotService.addMember(member));
    }

    @DemoSite
    @PreventDuplicateSubmissions
    @ApiOperation(value = "修改会员基本信息")
    @PutMapping
    public ResultMessage<Robot> update(@Valid ManagerRobotEditDTO managerMemberEditDTO, @RequestParam(required = false) List<String> tenantIds) {
        return ResultUtil.data(robotService.updateMember(managerMemberEditDTO,tenantIds));
    }

    @ApiOperation(value = "删除机器人")
    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable String id) {
        robotService.deleteRobotById(id);
    }
}
