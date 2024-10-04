package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CollectMessageEntity;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-10 23:08:59
 **/
public interface CollectMessageService extends IService<CollectMessageEntity> {
    AppPageUtils getCollectMessages(Integer currPage, String uid);

    void markMessagesAsRead(List<Long> messageIds);
}
