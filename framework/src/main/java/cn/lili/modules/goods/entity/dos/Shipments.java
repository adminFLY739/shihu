package cn.lili.modules.goods.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * 店铺发货地
 *
 * @author pikachu
 * @since 2020-02-18 15:18:56
 */
@Data
@TableName("li_shipments")
@ApiModel(value = "发货地")
public class Shipments extends BaseEntity {

  private static final long serialVersionUID = -1675137880294872580L;
  @NotEmpty(message = "发货地名称不能为空")
    @Length(max = 20, message = "发货地名称应该小于20长度字符")
    @ApiModelProperty(value = "发货地名称", required = true)
    private String name;

    @NotEmpty(message = "店铺id不能为空")
    @ApiModelProperty(value = "店铺id", required = true)
    private String storeId;

}
