package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-12-01 19:43:02
 **/
@Data
@TableName("lf_task")
public class TaskEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 任务id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 任务奖励
     */
    private String reward;
}
