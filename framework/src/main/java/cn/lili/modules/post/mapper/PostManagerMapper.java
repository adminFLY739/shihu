package cn.lili.modules.post.mapper;

import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.robot.entity.dos.Robot;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PostManagerMapper extends BaseMapper<PostEntity> {

    @Select("select * from lf_post ${ew.customSqlSegment}")
    IPage<PostEntity> pageByMember(IPage<PostEntity> page, @Param(Constants.WRAPPER) Wrapper<PostEntity> queryWrapper);
}