package cn.lili.modules.post.serviceImpl;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.post.entity.vo.PostVO;
import cn.lili.modules.post.mapper.PostManagerMapper;
import cn.lili.modules.post.service.PostManagerService;
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
public class PostManagerServiceImpl extends ServiceImpl<PostManagerMapper, PostEntity> implements PostManagerService {

    /**
     * 会员
     */
    @Autowired
    private RobotService robotService;

    @Override
    public IPage<PostVO> getMemberPage(PageVO page) {
        QueryWrapper<PostEntity> queryWrapper = Wrappers.query();

        // 按照创建时间降序排序
        queryWrapper.orderByDesc("create_time");

        // 执行分页查询会员信息
        IPage<PostEntity> memberPage = this.baseMapper.pageByMember(PageUtil.initPage(page), queryWrapper);

        List<PostVO> result = new ArrayList<>();

        // 遍历会员信息，转换为视图对象
        memberPage.getRecords().forEach(member -> {
            PostVO memberVO = new PostVO(member);
            result.add(memberVO);
        });
        // 构造返回的分页结果，
        Page<PostVO> pageResult = new Page(memberPage.getCurrent(), memberPage.getSize(), memberPage.getTotal());
        // 分页结果携带会员视图对象列表返回
        pageResult.setRecords(result);
        return pageResult;
    }

    @Override
    public void deleteRobotById(String id) {
        robotService.removeById(id);
    }
}