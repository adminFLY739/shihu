package cn.lili.modules.page.serviceimpl;


import cn.lili.modules.order.order.entity.vo.PaymentLog;
import cn.lili.modules.page.entity.dos.Feedback;
import cn.lili.modules.page.mapper.FeedbackMapper;
import cn.lili.modules.page.service.FeedbackService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 意见反馈业务层实现
 *
 * @author Chopper
 * @since 2020/11/18 11:40 上午
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Override
    public IPage<Feedback> queryFeedback(IPage<Feedback> page, Wrapper<Feedback> queryWrapper) {
        return baseMapper.queryFeedback(page, queryWrapper);
    }

}
