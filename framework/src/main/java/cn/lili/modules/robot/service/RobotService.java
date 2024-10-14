package cn.lili.modules.robot.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.robot.entity.dto.ManagerRobotEditDTO;
import cn.lili.modules.robot.entity.dto.RobotAddDTO;
import cn.lili.modules.robot.entity.vo.RobotSearchVO;
import cn.lili.modules.robot.entity.vo.RobotVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RobotService extends IService<Robot> {
    /**
     * 后台-添加会员
     *
     * @param memberAddDTO 会员
     * @return 会员
     */
    Robot addMember(RobotAddDTO memberAddDTO);

    /**
     * 后台-修改会员
     *
     * @param managerMemberEditDTO 后台修改会员参数
     * @param tenantIds            租户数组
     * @return 会员
     */
    Robot updateMember(ManagerRobotEditDTO managerMemberEditDTO , List<String> tenantIds);

    /**
     * 获取会员分页
     *
     * @param memberSearchVO 会员搜索VO
     * @param page           分页
     * @return 会员分页
     */
    IPage<RobotVO> getMemberPage(RobotSearchVO memberSearchVO, PageVO page);

    /**
     * 获取用户VO
     *
     * @param id 会员id
     * @return 用户VO
     */
    RobotVO getMember(String id);

    void deleteRobotById(String id);
}
