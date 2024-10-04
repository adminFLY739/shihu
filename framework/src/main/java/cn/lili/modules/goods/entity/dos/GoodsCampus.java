package cn.lili.modules.goods.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


/**
 * @author: ASUS
 * @since: 2023/4/12 09:11
 * @description:
 */
@Data
@TableName("li_goods_campus")
@ApiModel(value = "商品计量单位")
public class GoodsCampus extends BaseEntity {


    private static final long serialVersionUID = 4085907705966514195L;
    @NotEmpty(message = "计量单位名称不能为空")
    @ApiModelProperty(value = "计量单位名称")
    private String name;
}
