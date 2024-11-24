package cn.lili.modules.discuss.serviceImpl;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.BBS.entity.DiscussEntity;
import cn.lili.modules.discuss.entity.vo.DiscussVO;
import cn.lili.modules.discuss.mapper.DiscussManagerMapper;
import cn.lili.modules.discuss.service.DiscussManagerService;
import cn.lili.modules.robot.service.RobotService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiscussManagerServiceImpl extends ServiceImpl<DiscussManagerMapper, DiscussEntity> implements DiscussManagerService {

    /**
     * 会员
     */
    @Autowired
    private RobotService robotService;

    @Override
    public IPage<DiscussVO> getMemberPage(PageVO page) {
        QueryWrapper<DiscussEntity> queryWrapper = Wrappers.query();

        // 按照创建时间降序排序
        queryWrapper.orderByDesc("create_time");

        // 执行分页查询会员信息
        IPage<DiscussEntity> memberPage = this.baseMapper.pageByMember(PageUtil.initPage(page), queryWrapper);

        List<DiscussVO> result = new ArrayList<>();

        // 遍历会员信息，转换为视图对象
        memberPage.getRecords().forEach(member -> {

            DiscussVO memberVO = new DiscussVO(member);
            result.add(memberVO);
        });
        // 构造返回的分页结果，
        Page<DiscussVO> pageResult = new Page(memberPage.getCurrent(), memberPage.getSize(), memberPage.getTotal());
        // 分页结果携带会员视图对象列表返回
        pageResult.setRecords(result);
        System.out.println("resultresultresult" + result);
        return pageResult;
    }

    @Override
    public void deleteRobotById(String id) {
        robotService.removeById(id);
    }
}