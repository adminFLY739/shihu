package cn.lili.modules.member.mapper;

import cn.lili.modules.member.entity.dos.MemberPointsHistory;
import cn.lili.modules.member.entity.dto.MemberPointsHistoryParams;
import cn.lili.modules.member.entity.vo.MemberEvaluationListVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 会员积分历史数据处理层
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
public interface MemberPointsHistoryMapper extends BaseMapper<MemberPointsHistory> {

    /**
     * 获取所有用户的积分历史VO
     *
     * @param pointType 积分类型
     * @return
     */
    @Select("SELECT SUM( variable_point ) FROM li_member_points_history WHERE point_type = #{pointType}")
    Long getALLMemberPointsHistoryVO(String pointType);

    /**
     * 获取用户的积分数量
     *
     * @param pointType 积分类型
     * @param memberId  会员ID
     * @return 积分数量
     */
    @Select("SELECT SUM( variable_point ) FROM li_member_points_history WHERE point_type = #{pointType} AND member_id=#{memberId}")
    Long getMemberPointsHistoryVO(String pointType, String memberId);

    /**
     * 会员积分历史分页
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 会员评价分页
     */
    @Select("select me.*  FROM li_member_points_history as me join li_member as m on me.member_id = m.id  ${ew.customSqlSegment} ")
    IPage<MemberPointsHistory> getMemberPointsHistoryList(IPage<MemberEvaluationListVO> page, @Param(Constants.WRAPPER) Wrapper<MemberPointsHistoryParams> queryWrapper);


}
