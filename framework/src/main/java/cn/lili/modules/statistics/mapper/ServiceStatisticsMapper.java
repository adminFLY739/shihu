package cn.lili.modules.statistics.mapper;

import cn.lili.modules.statistics.entity.dos.ServiceStatistics;
import cn.lili.modules.statistics.entity.vo.ServiceStatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/8/16 10:12
 * @description: 业务接口统计数据层3
 */
public interface ServiceStatisticsMapper extends BaseMapper<ServiceStatistics> {

    @Select("SELECT count,type,name ,service_statistics.update_time FROM service_statistics left join tenant on service_statistics.tenant_id = tenant.id ${ew.customSqlSegment}")
    IPage<ServiceStatisticsVO> getServiceStatisticsData(Page<Object> initPage, @Param(Constants.WRAPPER) QueryWrapper<ServiceStatistics> queryWrapper);
}
