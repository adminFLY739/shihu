package cn.lili.modules.BBS.service;

import cn.lili.modules.BBS.entity.FollowDiscussEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author wuwenxin
 * @date 2023-10-25 22:14:30
 **/
public interface FollowDiscussService extends IService<FollowDiscussEntity> {

    Boolean isFollowOrNot(String uid, Integer discussId);

    List<Integer> getFollowDiscussIdsByUid(String uid);

    Integer getFollowCountByDiscussId(Integer discussId);
}
