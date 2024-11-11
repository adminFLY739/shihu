package cn.lili.modules.post.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.post.entity.vo.PostVO;
import cn.lili.modules.robot.entity.dos.Robot;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PostManagerService extends IService<PostEntity> {

    IPage<PostVO> getMemberPage(PageVO page);


    void deleteRobotById(String id);
}
