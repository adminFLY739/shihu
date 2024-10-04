package cn.lili.modules.BBS.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class AddDiscussForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 话题id
     */
    private Integer id;

    /**
     * 标题
     */
    @Length(max = 200, message = "标题不能超过200个字符")
    @NotBlank(message = "参数有误")
    private String title;

    /**
     * 内容
     */
    @Length(max = 1000, message = "内容不能超过1000个字符")
    @NotBlank(message = "参数有误")
    private String description;

    /**
     * 分类id
     */
    private List<Integer> cut;
}
