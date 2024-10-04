package cn.lili.modules.statistics.service;

import cn.lili.modules.member.entity.vo.MemberDistributionVO;
import cn.lili.modules.statistics.entity.dos.PlatformViewData;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import cn.lili.modules.statistics.entity.vo.OnlineMemberVO;
import cn.lili.modules.statistics.entity.vo.PlatformViewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 平台PV统计
 *
 * @author Bulbasaur
 * @since 2020/12/9 11:06
 */
public interface PlatformViewService extends IService<PlatformViewData> {


    /**
     * 当前在线人数
     *
     * @return 当前在线人数
     */
    Long online();

    /**
     * 会员分布
     *
     * @return 会员分布
     */
    List<MemberDistributionVO> memberDistribution();

    /**
     * 在线人数记录
     *
     * @return 在线人数
     */
    List<OnlineMemberVO> statisticsOnline();

    /**
     * 数据查询
     *
     * @param queryParam 查找参数
     * @return 流量数据
     */
    List<PlatformViewVO> list(StatisticsQueryParam queryParam);

    /**
     * 查询累计访客数
     *
     * @param queryParam  查询参数
     * @return 累计访客数
     */
    Integer countUv(StatisticsQueryParam queryParam);
}
