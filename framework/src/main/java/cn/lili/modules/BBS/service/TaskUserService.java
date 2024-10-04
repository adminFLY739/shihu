package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.TaskUserEntity;
import cn.lili.modules.BBS.entity.vo.TaskListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-12-01 19:36:19
 **/
public interface TaskUserService extends IService<TaskUserEntity> {

    List<TaskListResponse> getTaskList(String uid);

    boolean isComplete(String uid,String taskId);

    boolean isTaskUserExist(String uid);

    void addTaskUser(String uid, String taskId, Long point, String content);
}
