package cn.lili.timetask.handler;

/**
 * 每日任务
 * 每日凌晨00:00点执行
 *
 * @author wuwenxin
 * @date 2023-12-03 12:24:08
 **/
public interface EveryDayZeroAMExecute {

    /**
     * 执行每日任务
     */
    void execute();
}
