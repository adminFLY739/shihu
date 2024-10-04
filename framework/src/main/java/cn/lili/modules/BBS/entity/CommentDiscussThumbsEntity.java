package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuwenxin
 * @date 2023-10-21 20:02:42
 **/
@Data
@TableName("lf_comment_discuss_thumbs")
public class CommentDiscussThumbsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 评论id
     */
    private Integer commentDiscussId;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 创建时间
     */
    private Date createTime;

}
