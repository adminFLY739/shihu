package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CommentMessageEntity;
import cn.lili.modules.BBS.entity.vo.MessageCountResponse;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-07 18:26:58
 **/
public interface CommentMessageService extends IService<CommentMessageEntity> {

    AppPageUtils getCommentMessages(Integer currPage,String uid);

    void markMessagesAsRead(List<Long> messageIds);

    MessageCountResponse getMessageNoReadCount(String uid);

    Integer getAllMessageNoReadCount(String uid);
}
