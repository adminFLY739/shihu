package cn.lili.controller.BBS;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.R;
import cn.lili.modules.BBS.entity.vo.TaskListResponse;
import cn.lili.modules.BBS.service.TaskUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-12-01 16:35:55
 **/
@RestController
@RequestMapping("buyer/bbs/task")
@Api(tags = "每日任务接口")
public class APPTaskController {

    @Resource
    private TaskUserService  taskUserService;

    /**
     * 每日任务列表
     */
    @GetMapping("/getTaskList")
    @ApiOperation("获取每日任务")
    public R getTaskList() {
        AuthUser authUser = UserContext.getCurrentUser();
        if(authUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        List<TaskListResponse> list = taskUserService.getTaskList(authUser.getId());
        return R.ok().put("result",list);
    }
}
