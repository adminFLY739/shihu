package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-10-25 22:11:53
 **/
@Data
@TableName("lf_follow_discuss")
public class FollowDiscussEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 关注的话题id
     */
    private Integer discussId;
    /**
     * 创建时间
     */
    private Date createTime;
}
