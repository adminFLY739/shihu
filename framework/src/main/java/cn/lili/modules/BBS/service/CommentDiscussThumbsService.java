package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.CommentDiscussEntity;
import cn.lili.modules.BBS.entity.CommentDiscussThumbsEntity;
import cn.lili.modules.BBS.entity.CommentEntity;
import cn.lili.modules.BBS.param.AddThumbsForm;
import cn.lili.modules.BBS.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author wuwenxin
 * @date 2023-10-21 20:01:31
 **/
public interface CommentDiscussThumbsService extends IService<CommentDiscussThumbsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean isThumbs(String uid, Long id);

    Integer getThumbsCount(Long id);

    void addThumbs(AddThumbsForm addThumbsForm, String uid);

    void cancelThumbs(AddThumbsForm addThumbsForm, String uid);

    void cancelAllThumbs(List<CommentDiscussEntity> commentDiscussList);
}
