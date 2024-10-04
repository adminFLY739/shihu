package cn.lili.modules.BBS.service.impl;

import cn.lili.modules.BBS.entity.TaskEntity;
import cn.lili.modules.BBS.entity.TaskUserEntity;
import cn.lili.modules.BBS.entity.vo.TaskListResponse;
import cn.lili.modules.BBS.mapper.TaskDao;
import cn.lili.modules.BBS.mapper.TaskUserDao;
import cn.lili.modules.BBS.service.TaskUserService;
import cn.lili.modules.member.entity.enums.PointTypeEnum;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-12-01 19:37:35
 **/
@Service
public class TaskUserServiceImpl extends ServiceImpl<TaskUserDao, TaskUserEntity> implements TaskUserService {

    @Resource
    private TaskUserDao taskUserDao;
    @Resource
    private TaskDao taskDao;
    @Resource
    private MemberService memberService;

    @Override
    public List<TaskListResponse> getTaskList(String uid) {
        List<TaskEntity> taskEntities = taskDao.selectList(null);
        ArrayList<TaskListResponse> responses = new ArrayList<>();

        taskEntities.forEach((task) -> {
            TaskListResponse taskListResponse = new TaskListResponse();
            taskListResponse.setId(task.getId());
            taskListResponse.setDescription(task.getDescription());
            taskListResponse.setReward(task.getReward());
            taskListResponse.setIsComplete(this.isComplete(uid, task.getId().toString()));

            responses.add(taskListResponse);
        });
        return responses;
    }

    @Override
    public boolean isComplete(String uid, String taskId) {
        QueryWrapper<TaskUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.like("task_ids", taskId);
        return taskUserDao.exists(queryWrapper);
    }

    @Override
    public boolean isTaskUserExist(String uid) {
        QueryWrapper<TaskUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return taskUserDao.exists(queryWrapper);
    }

    /**
     * 每日任务
     */
    @Override
    public void addTaskUser(String uid, String taskId, Long point, String content) {
        // 判断是否达到每日任务要求，如果达到且任务未完成则完成每日任务
        QueryWrapper<TaskUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        TaskUserEntity taskUser = this.getOne(queryWrapper);
        if (taskUser == null) {
            taskUser = new TaskUserEntity();
            taskUser.setUid(uid);
            taskUser.setTaskIds(null);
            taskUser.setCreateTime(new Date());
            this.save(taskUser);
        }
        // 如果用户已完成该任务则不做处理，否则更新用户任务完成状态
        if (taskUser.getTaskIds() == null || !taskUser.getTaskIds().contains(taskId)) {
            List<String> taskIds;
            if (taskUser.getTaskIds() == null){
                taskIds = new ArrayList<>();
            }else {
                taskIds = new ArrayList<>(Arrays.asList(taskUser.getTaskIds().split(",")));
            }

            taskIds.add(taskId);
            taskUser.setTaskIds(String.join(",", taskIds));
            this.updateById(taskUser);

            // 给用户增加积分
            memberService.updateMemberPoint(point, PointTypeEnum.INCREASE.name(), uid, content + "，赠送积分" + point + "分");
        }
    }
}
