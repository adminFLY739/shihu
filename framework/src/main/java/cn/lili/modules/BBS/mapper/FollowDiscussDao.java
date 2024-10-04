package cn.lili.modules.BBS.mapper;

import cn.lili.modules.BBS.entity.FollowDiscussEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wuwenxin
 * @date 2023-10-25 22:13:28
 **/
@Mapper
public interface FollowDiscussDao  extends BaseMapper<FollowDiscussEntity> {

    @Delete("delete from lf_follow_discuss where uid=#{uid} and discuss_id=#{discussId}")
    void cancelFollowDiscuss(@Param("uid")String uid, @Param("discussId")Integer discussId);
}
