package cn.lili.modules.BBS.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wuwenxin
 * @date 2023-11-10 14:38:49
 **/
@Data
@TableName("lf_post_thumb")
public class PostThumbEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
//	@TableId
    private String uid;
    /**
     * 帖子id
     */
    private Integer postId;
}
