package cn.lili.modules.page.mapper;

import cn.lili.modules.order.order.entity.vo.PaymentLog;
import cn.lili.modules.page.entity.dos.Feedback;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 意见反馈处理层
 *
 * @author pikachu
 * @since 2020-05-06 15:18:56
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {
    /**
     * 查询意见反馈
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    @Select("select * from li_feedback  ${ew.customSqlSegment} ")
    IPage<Feedback> queryFeedback(IPage<Feedback> page, @Param(Constants.WRAPPER) Wrapper<Feedback> queryWrapper);
}
