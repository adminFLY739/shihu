package cn.lili.modules.statistics.entity.dos;

import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: nxc
 * @since: 2023/8/16 10:02
 * @description: 统计不同接口的调用次数
 */

@Data
@TableName("service_statistics")
public class ServiceStatistics  extends BaseEntity {

  @ApiModelProperty(value = "调用数量")
  private Long count;

  @ApiModelProperty(value= "租户id")
  private String tenantId;

  /**
   * @see cn.lili.modules.statistics.entity.enums.ServiceTypeEnum
   */
  @ApiModelProperty(value= "业务类型")
  private String type;
}
