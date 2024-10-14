package cn.lili.modules.robot.mapper;

import cn.lili.modules.robot.entity.dos.Robot;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RobotMapper extends BaseMapper<Robot> {
    /**
     * 获取所有的会员手机号
     *
     * @return 会员手机号
     */
    @Select("select m.mobile from lf_robot m")
    List<String> getAllMemberMobile();

    @Select("select * from lf_robot ${ew.customSqlSegment}")
    IPage<Robot> pageByMember(IPage<Robot> page, @Param(Constants.WRAPPER) Wrapper<Robot> queryWrapper);

    @Update("UPDATE lf_robot SET username=#{username},student_id=#{studentId} WHERE id=#{id}")
    boolean updateMemberUsernameAndStudentIdById(String id, String username, String studentId);
}