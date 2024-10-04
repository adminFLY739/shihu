package cn.lili.modules.BBS.mapper;

import cn.lili.modules.BBS.entity.TaskUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author wuwenxin
 * @date 2023-12-01 19:38:33
 **/
@Mapper
public interface TaskUserDao extends BaseMapper<TaskUserEntity> {

    @Update("truncate table lf_task_user")
    void truncateTaskUserTable();
}
