package cn.lili.timetask.handler.impl.BBS;

import cn.lili.modules.BBS.mapper.TaskUserDao;
import cn.lili.timetask.handler.EveryDayZeroAMExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wuwenxin
 * @date 2023-12-03 12:00:15
 **/
@Slf4j
@Component
public class ResetDailyTaskExecute implements EveryDayZeroAMExecute {

    @Resource
    private TaskUserDao taskUserDao;

    @Override
    public void execute() {
        log.info("定时任务：重置每日任务");
        taskUserDao.truncateTaskUserTable();
    }
}
