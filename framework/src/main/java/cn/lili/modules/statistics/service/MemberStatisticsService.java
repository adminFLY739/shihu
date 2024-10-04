package cn.lili.modules.statistics.service;


import cn.lili.modules.member.entity.vo.MemberDistributionVO;
import cn.lili.modules.statistics.entity.dos.MemberStatisticsData;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * 会员统计业务层
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:06
 */
public interface MemberStatisticsService extends IService<MemberStatisticsData> {

    /**
     * 获取会员数量
     *
     * @return 会员统计
     */
    long getMemberCount(String tenantId);

    /**
     * 获取今日新增会员数量
     *
     * @return 今日新增会员数量
     */
    long todayMemberNum();

    /**
     * 获取指定结束时间前的会员数量
     *
     * @param endTime  结束时间
     * @param tenantId 租户id
     * @return 会员数量
     */
    long memberCount(Date endTime,String tenantId);

    /**
     * 当天活跃会员数量
     *
     * @param startTime  开始时间
     * @param tenantId   租户id
     * @return 活跃会员数量
     */
    long activeQuantity(Date startTime,String tenantId);

    /**
     * 时间段内新增会员数量
     *
     * @param endTime   结束时间
     * @param startTime 开始时间
     * @param tenantId  租户id
     * @return 增加会员数量
     */
    long newlyAdded(Date endTime, Date startTime,String tenantId);

    /**
     * 根据参数，查询这段时间的会员统计
     *
     * @param statisticsQueryParam 查找参数
     * @return 会员统计
     */
    List<MemberStatisticsData> statistics(StatisticsQueryParam statisticsQueryParam);


    /**
     * 查看会员数据分布
     *
     * @return 会员数据分布
     */
    List<MemberDistributionVO> distribution();




}
