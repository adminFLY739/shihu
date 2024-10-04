package cn.lili.modules.statistics.service;


import cn.lili.common.vo.PageVO;
import cn.lili.modules.statistics.entity.dos.ServiceStatistics;
import cn.lili.modules.statistics.entity.vo.ServiceStatisticsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: nxc
 * @since: 2023/8/16 10:10
 * @description: 业务接口统计服务层
 */
public interface ServiceStatisticsService extends IService<ServiceStatistics> {

      /**
      *
      * 次数统计
      *
      *@param type  类型
      *@param tenantId 租户id
      */
      void addServiceCount(String type , String tenantId);

      /**
      *
      * 统计OSS使用
      *
      *@param size 大小
      *@param tenantId 租户id
      */
      void addOSSServiceCount(Long size , String tenantId);

      /**
      *
      * 分页查询
      *
      *@param pageVO 份额有参数
      *@param type   类型
      *@return: 查询结果
      */
     IPage<ServiceStatisticsVO> getServiceStatisticsData(PageVO pageVO, String type);
}
