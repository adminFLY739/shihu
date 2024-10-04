package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.PostOpposeEntity;
import cn.lili.modules.BBS.param.AddPostOpposeForm;
import cn.lili.modules.BBS.param.AddPostThumbForm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author wuwenxin
 * @date 2023-11-10 14:44:39
 **/
public interface PostOpposeService extends IService<PostOpposeEntity> {

    Integer opposeCount(Integer postId);

    Boolean isPostOppose(String uid,Integer postId);

    void cancelPostOppose(AddPostOpposeForm request, String uid);

    void addPostOppose(AddPostOpposeForm request, String uid);
}
