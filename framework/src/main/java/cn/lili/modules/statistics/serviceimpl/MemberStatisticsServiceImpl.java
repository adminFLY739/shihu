package cn.lili.modules.statistics.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.vo.MemberDistributionVO;
import cn.lili.modules.member.entity.vo.MemberSearchVO;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.statistics.entity.dos.MemberStatisticsData;
import cn.lili.modules.statistics.entity.dto.StatisticsQueryParam;
import cn.lili.modules.statistics.entity.enums.SearchTypeEnum;
import cn.lili.modules.statistics.mapper.MemberStatisticsMapper;
import cn.lili.modules.statistics.service.MemberStatisticsService;
import cn.lili.modules.statistics.util.StatisticsDateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 会员统计业务层实现
 *
 * @author Bulbasaur
 * @since 2020/12/9 18:33
 */
@Service
public class MemberStatisticsServiceImpl extends ServiceImpl<MemberStatisticsMapper, MemberStatisticsData> implements MemberStatisticsService {


    @Autowired
    private MemberService memberService;

    @Override
    public long getMemberCount(String tenantId) {
        MemberSearchVO memberSearchVO = new MemberSearchVO();
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            memberSearchVO.setTenantId(tenantId);
        }
        memberSearchVO.setDisabled(SwitchEnum.OPEN.name());
        return memberService.getMemberNum(memberSearchVO);
    }

    @Override
    public long todayMemberNum() {
        QueryWrapper<Member> queryWrapper = Wrappers.query();
        queryWrapper.ge("create_time", DateUtil.beginOfDay(new Date()));
        return this.baseMapper.customSqlQuery(queryWrapper);
    }

    @Override
    public long memberCount(Date endTime,String tenantId) {
        QueryWrapper<Member> queryWrapper = Wrappers.query();
        queryWrapper.like("tenant_ids",tenantId);
        queryWrapper.le("create_time", endTime);
        return this.baseMapper.customSqlQuery(queryWrapper);
    }

    @Override
    public long activeQuantity(Date startTime,String tenantId) {

        QueryWrapper<Member> queryWrapper = Wrappers.query();
        queryWrapper.like("tenant_ids",tenantId);
        queryWrapper.ge("last_login_date", startTime);
        return this.baseMapper.customSqlQuery(queryWrapper);
    }

    @Override
    public long newlyAdded(Date startTime, Date endTime,String tenantId) {
        QueryWrapper<Member> queryWrapper = Wrappers.query();
        queryWrapper.like("tenant_ids",tenantId);
        queryWrapper.between("create_time", startTime, endTime);
        return this.baseMapper.customSqlQuery(queryWrapper);
    }

    @Override
    public List<MemberStatisticsData> statistics(StatisticsQueryParam statisticsQueryParam) {

        Date[] dates = StatisticsDateUtil.getDateArray(statisticsQueryParam);
        Date startTime = dates[0];
        Date endTime = dates[1];

        //如果统计今天，则自行构造数据
        if (statisticsQueryParam.getSearchType().equals(SearchTypeEnum.TODAY.name())) {
            //构建数据，然后返回集合，提供给前端展示
            MemberStatisticsData memberStatisticsData = new MemberStatisticsData();
            memberStatisticsData.setMemberCount(this.memberCount(endTime,statisticsQueryParam.getTenantId()));
            memberStatisticsData.setCreateDate(startTime);
            memberStatisticsData.setTenantId(statisticsQueryParam.getTenantId());
            memberStatisticsData.setActiveQuantity(this.activeQuantity(startTime,statisticsQueryParam.getTenantId()));
            memberStatisticsData.setNewlyAdded(this.newlyAdded(startTime, endTime,statisticsQueryParam.getTenantId()));
            List<MemberStatisticsData> result = new ArrayList<>();
            result.add(memberStatisticsData);
            return result;
        }

        QueryWrapper<MemberStatisticsData> queryWrapper = Wrappers.query();
        queryWrapper.eq("tenant_id",statisticsQueryParam.getTenantId());
        queryWrapper.between("create_date", startTime, endTime);

        return this.list(queryWrapper);
    }


    @Override
    public List<MemberDistributionVO> distribution() {
        return this.baseMapper.distribution();
    }

}
