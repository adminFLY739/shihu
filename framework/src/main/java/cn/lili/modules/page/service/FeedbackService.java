package cn.lili.modules.page.service;

import cn.lili.modules.page.entity.dos.Feedback;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 意见反馈业务层
 *
 * @author pikachu
 * @since 2020/11/18 11:40 上午
 */
public interface FeedbackService extends IService<Feedback> {
    /**
     * 查询订单支付记录
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 订单支付记录分页
     */
    IPage<Feedback> queryFeedback(IPage<Feedback> page, Wrapper<Feedback> queryWrapper);

}
