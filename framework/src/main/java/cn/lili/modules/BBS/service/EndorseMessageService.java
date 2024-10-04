package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.EndorseMessageEntity;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-09 22:32:55
 **/
public interface EndorseMessageService extends IService<EndorseMessageEntity> {

    AppPageUtils getEndorseMessages(Integer currPage, String uid);

    void markMessagesAsRead(List<Long> messageIds);

}
