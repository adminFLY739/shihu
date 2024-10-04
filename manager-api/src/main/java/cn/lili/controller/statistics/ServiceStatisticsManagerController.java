package cn.lili.controller.statistics;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import cn.lili.modules.statistics.entity.vo.RefundOrderStatisticsDataVO;
import cn.lili.modules.statistics.entity.vo.ServiceStatisticsVO;
import cn.lili.modules.statistics.service.ServiceStatisticsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: nxc
 * @since: 2023/8/16 10:50
 * @description: 业务接口统计控制层
 */

@Api(tags = "管理端,业务统计接口")
@RestController
@RequestMapping("/manager/statistics/service")
public class ServiceStatisticsManagerController {

  @Autowired
  private ServiceStatisticsService serviceStatisticsService;
  @ApiOperation(value = "获取业务统计列表")
  @GetMapping("/getByPage/{type}")
  public ResultMessage<IPage<ServiceStatisticsVO>> getByPage(PageVO pageVO, @PathVariable String type) {
    return ResultUtil.data(serviceStatisticsService.getServiceStatisticsData(pageVO, type));
  }
}
