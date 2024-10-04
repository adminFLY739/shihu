package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.InviteMessageEntity;
import cn.lili.modules.BBS.param.AddInviteForm;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-11-13 16:42:42
 **/
public interface InviteMessageService extends IService<InviteMessageEntity> {
    AppPageUtils getInviteMessages(Integer currPage, String uid);

    void markMessagesAsRead(List<Long> messageIds);

    void addInviteMessage(AddInviteForm addInviteForm,String uid);
}
