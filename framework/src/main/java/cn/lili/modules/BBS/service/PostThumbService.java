package cn.lili.modules.BBS.service;


import cn.lili.modules.BBS.entity.PostThumbEntity;
import cn.lili.modules.BBS.param.AddPostThumbForm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author wuwenxin
 * @date 2023-11-10 14:42:50
 **/
public interface PostThumbService extends IService<PostThumbEntity> {

    Integer thumbCount(Integer postId);

    Boolean isPostThumb(String uid,Integer postId);

    void cancelPostThumb(AddPostThumbForm request, String uid);

    void addPostThumb(AddPostThumbForm request, String uid);
}
