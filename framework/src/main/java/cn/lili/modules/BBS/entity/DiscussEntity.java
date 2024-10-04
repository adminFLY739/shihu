package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wwx
 * @date 2023/10/16 23:00:25
 */
@Data
@TableName("lf_discuss")
public class DiscussEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题作者ID
     */
    private String uid;

    /**
     * 话题内容
     */
    private String description;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 评论量
     */
    private Integer postCount;

    /**
     * 浏览量
     */
    private Integer readCount;

    /**
     * 话题标签id
     */
    private String cut;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 置顶优先级
     */
    private Integer discussTop;
}
