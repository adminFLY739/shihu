package cn.lili.modules.member.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.member.entity.dos.MemberPointsHistory;
import cn.lili.modules.member.entity.dto.MemberPointsHistoryParams;
import cn.lili.modules.member.entity.vo.MemberPointsHistoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员积分历史业务层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface MemberPointsHistoryService extends IService<MemberPointsHistory> {

    /**
     * 获取会员积分VO
     *
     * @param memberId 会员ID
     * @return 会员积分VO
     */
    MemberPointsHistoryVO getMemberPointsHistoryVO(String memberId);

    /**
     * 会员积分历史
     *
     * @param page       分页
     * @param memberPointsHistoryParams   用户积分历史查询参数
     * @return 积分历史分页
     */
    IPage<MemberPointsHistory> MemberPointsHistoryList(PageVO page, MemberPointsHistoryParams memberPointsHistoryParams);

}
