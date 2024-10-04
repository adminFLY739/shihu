package cn.lili.modules.statistics.serviceimpl;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.statistics.entity.dos.ServiceStatistics;
import cn.lili.modules.statistics.entity.enums.ServiceTypeEnum;
import cn.lili.modules.statistics.entity.vo.ServiceStatisticsVO;
import cn.lili.modules.statistics.mapper.ServiceStatisticsMapper;

import cn.lili.modules.statistics.service.ServiceStatisticsService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author: nxc
 * @since: 2023/8/16 10:12
 * @description: 业务统计业务实现层
 */

@Service
public class ServiceStatisticsImpl extends ServiceImpl<ServiceStatisticsMapper, ServiceStatistics> implements ServiceStatisticsService {


  @Override
  public void addServiceCount(String type, String tenantId) {
    LambdaQueryWrapper<ServiceStatistics> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ServiceStatistics::getType,type);
    queryWrapper.eq(ServiceStatistics::getTenantId,tenantId);
    ServiceStatistics serviceStatistics = this.getOne(queryWrapper);
    if(serviceStatistics==null){
      serviceStatistics=new ServiceStatistics();
      serviceStatistics.setCount(1L);
      serviceStatistics.setType(type);
      serviceStatistics.setTenantId(tenantId);
      this.save(serviceStatistics);
    }
    else{
      serviceStatistics.setCount(serviceStatistics.getCount()+1);
      this.updateById(serviceStatistics);
    }
  }

  @Override
  public void addOSSServiceCount(Long size, String tenantId) {
    LambdaQueryWrapper<ServiceStatistics> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ServiceStatistics::getType, ServiceTypeEnum.OSS.name());
    queryWrapper.eq(ServiceStatistics::getTenantId,tenantId);
    ServiceStatistics serviceStatistics = this.getOne(queryWrapper);
    if(serviceStatistics==null){
      serviceStatistics=new ServiceStatistics();
      serviceStatistics.setCount(size);
      serviceStatistics.setType(ServiceTypeEnum.OSS.name());
      serviceStatistics.setTenantId(tenantId);
      this.save(serviceStatistics);
    }
    else{
      serviceStatistics.setCount(serviceStatistics.getCount()+size);
      this.updateById(serviceStatistics);
    }
  }

  @Override
  public IPage<ServiceStatisticsVO> getServiceStatisticsData(PageVO pageVO, String type) {
    QueryWrapper<ServiceStatistics> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type",type);
    return this.baseMapper.getServiceStatisticsData(PageUtil.initPage(pageVO), queryWrapper);
  }
}
