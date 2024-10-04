package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.FollowMessageEntity;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-11 16:03:08
 **/
public interface FollowMessageService extends IService<FollowMessageEntity> {

    AppPageUtils getFollowMessages(Integer currPage, String uid);

    void markMessagesAsRead(List<Long> messageIds);
}
