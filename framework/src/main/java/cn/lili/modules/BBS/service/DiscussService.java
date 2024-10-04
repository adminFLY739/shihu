package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.DiscussEntity;
import cn.lili.modules.BBS.entity.vo.DiscussDetailResponse;
import cn.lili.modules.BBS.param.*;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author wuwenxin
 * @date 2023-10-16 23:03:22
 */
public interface DiscussService extends IService<DiscussEntity> {

    AppPageUtils lastDiscuss(Integer currPage);

    DiscussDetailResponse detail(Integer id,String uid);

    Long addDiscuss(AddDiscussForm addDiscussForm, String uid);

    Long updateDiscuss(AddDiscussForm updateDiscussForm, String uid);

    void addCommentDiscuss(AddCommentDiscussForm addCommentDiscussForm, String uid);

    void addFollowDiscuss(AddFollowDiscussForm addFollowDiscussForm, String uid);

    void cancelFollowDiscuss(AddFollowDiscussForm addFollowDiscussForm, String uid);

    AppPageUtils myFollowDiscuss(Integer currPage,String uid);

    void delCommentDiscuss(DelCommentDiscussForm request, String uid);
}
