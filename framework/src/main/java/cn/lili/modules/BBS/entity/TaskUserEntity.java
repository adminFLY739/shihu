package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-12-01 19:20:56
 **/
@Data
@TableName("lf_task_user")
public class TaskUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 完成的任务id
     */
    private String taskIds;

    /**
     * 创建时间
     */
    private Date createTime;
}
