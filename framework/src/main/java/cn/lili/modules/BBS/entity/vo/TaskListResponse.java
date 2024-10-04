package cn.lili.modules.BBS.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuwenxin
 * @date 2023-12-01 18:43:14
 **/
@Data
public class TaskListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    private Integer id;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 任务奖励
     */
    private String reward;
    /**
     * 是否完成
     */
    private Boolean isComplete;
}
