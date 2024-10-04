package cn.lili.modules.member.serviceimpl;


import cn.lili.common.vo.PageVO;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dos.MemberPointsHistory;
import cn.lili.modules.member.entity.dto.MemberPointsHistoryParams;
import cn.lili.modules.member.entity.vo.MemberPointsHistoryVO;
import cn.lili.modules.member.mapper.MemberPointsHistoryMapper;
import cn.lili.modules.member.service.MemberPointsHistoryService;
import cn.lili.modules.member.service.MemberService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会员积分历史业务层实现
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Service
public class MemberPointsHistoryServiceImpl extends ServiceImpl<MemberPointsHistoryMapper, MemberPointsHistory> implements MemberPointsHistoryService {


    @Autowired
    private MemberService memberService;
    /**
     * 用户积分历史数据层
     */
    @Resource
    private  MemberPointsHistoryMapper  memberPointsHistoryMapper;

    @Override
    public MemberPointsHistoryVO getMemberPointsHistoryVO(String memberId) {
        //获取会员积分历史
        Member member = memberService.getById(memberId);
        MemberPointsHistoryVO memberPointsHistoryVO = new MemberPointsHistoryVO();
        if (member != null) {
            memberPointsHistoryVO.setPoint(member.getPoint());
            memberPointsHistoryVO.setTotalPoint(member.getTotalPoint());
            return memberPointsHistoryVO;
        }
        return new MemberPointsHistoryVO();
    }

    @Override
    public IPage<MemberPointsHistory> MemberPointsHistoryList(PageVO page, MemberPointsHistoryParams memberPointsHistoryParams) {

        return memberPointsHistoryMapper.getMemberPointsHistoryList(PageUtil.initPage(memberPointsHistoryParams), memberPointsHistoryParams.queryWrapper());
    }

}
