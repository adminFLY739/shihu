package cn.lili.modules.statistics.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: nxc
 * @since: 2023/8/16 10:02
 * @description: 统计不同接口的调用次数
 */

@Data
public class ServiceStatisticsVO  {

  @ApiModelProperty(value = "调用数量")
  private Long count;

  @ApiModelProperty(value= "租户名称")
  private String name;

  /**
   * @see cn.lili.modules.statistics.entity.enums.ServiceTypeEnum
   */
  @ApiModelProperty(value= "业务类型")
  private String type;

  @LastModifiedDate
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  @ApiModelProperty(value = "更新时间", hidden = true)
  @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss|| yyyy/MM/dd ||epoch_millis")
  private Date updateTime;
}
