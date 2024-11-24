package cn.lili.modules.discuss.mapper;

import cn.lili.modules.BBS.entity.DiscussEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface DiscussManagerMapper extends BaseMapper<DiscussEntity> {

    @Select("select * from lf_discuss ${ew.customSqlSegment}")
    IPage<DiscussEntity> pageByMember(IPage<DiscussEntity> page, @Param(Constants.WRAPPER) Wrapper<DiscussEntity> queryWrapper);
}