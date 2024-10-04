package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CommentDiscussEntity;
import cn.lili.modules.BBS.utils.AppPageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentDiscussService extends IService<CommentDiscussEntity> {

    Integer getCommentDiscussCountByDiscussId(Integer id);

    AppPageUtils queryCommentPage(Integer discussId, Integer page);

    void deleteCommentDiscussById(Long id);
}
