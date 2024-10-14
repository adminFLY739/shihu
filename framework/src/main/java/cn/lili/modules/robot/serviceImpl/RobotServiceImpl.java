package cn.lili.modules.robot.serviceImpl;


import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.common.event.TransactionCommitSendMQEvent;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.sensitive.SensitiveWordsFilter;
import cn.lili.common.utils.*;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.connect.service.ConnectService;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.robot.entity.vo.RobotSearchVO;
import cn.lili.modules.robot.entity.vo.RobotVO;
import cn.lili.modules.robot.entity.dto.ManagerRobotEditDTO;
import cn.lili.modules.robot.entity.dto.RobotAddDTO;
import cn.lili.modules.robot.mapper.RobotMapper;
import cn.lili.modules.member.service.ClerkService;
import cn.lili.modules.member.service.MemberTenantService;
import cn.lili.modules.member.token.MemberTokenGenerate;
import cn.lili.modules.member.token.StoreTokenGenerate;
import cn.lili.modules.robot.service.RobotService;
import cn.lili.modules.sms.SmsUtil;
import cn.lili.modules.store.service.StoreDetailService;
import cn.lili.modules.store.service.StoreService;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

// import static cn.lili.modules.verification.entity.enums.VerificationEnums.*;

/**
 * 会员接口业务层实现
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
@Service
public class RobotServiceImpl extends ServiceImpl<RobotMapper, Robot> implements RobotService {

    /**
     * 会员token
     */
    @Autowired
    private MemberTokenGenerate memberTokenGenerate;
    /**
     * 用户租户
     */
    @Autowired
    private MemberTenantService memberTenantService;
    /**
     * 商家token
     */
    @Autowired
    private StoreTokenGenerate storeTokenGenerate;
    /**
     * 店铺租户
     */
    @Autowired
    private StoreTenantService storeTenantService;

    /**
     * 店员
     */
    @Autowired
    private ClerkService clerkService;

    /**
     * 联合登录
     */
    @Autowired
    private ConnectService connectService;
    /**
     * 店铺
     */
    @Autowired
    private StoreService storeService;

    /**
     * 会员
     */
    @Autowired
    private RobotService robotService;

    @Resource
    private RobotMapper robotMapper;

    /**
     * 店铺详情
     */
    @Autowired
    private StoreDetailService storeDetailService;

    /**
     * 租户
     */
    @Autowired
    private TenantAreaService tenantAreaService;
    /**
     * RocketMQ 配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    /**
     * 缓存
     */
    @Autowired
    private Cache cache;

    @Autowired
    private SmsUtil smsUtil;
    @Transactional
    public void registerHandler(Robot member) {
        member.setId(SnowFlake.getIdStr());
        //保存会员
        this.save(member);

        // 发送会员注册信息
        applicationEventPublisher.publishEvent(new TransactionCommitSendMQEvent("new member register", rocketmqCustomProperties.getMemberTopic(), MemberTagsEnum.MEMBER_REGISTER.name(), member));
    }
    @Override
    @Transactional
    public Robot addMember(RobotAddDTO memberAddDTO) {
        Robot robot = new Robot(memberAddDTO.getNickName(), memberAddDTO.getUsername(), new BCryptPasswordEncoder().encode(memberAddDTO.getPassword()), memberAddDTO.getMobile(), memberAddDTO.getTenantIds());
        registerHandler(robot);
        memberTenantService.updateMemberTenantStatusByManager(robot.getId(), Collections.singletonList(memberAddDTO.getTenantIds()));
        return robot;
    }

    @Override
    public Robot updateMember(ManagerRobotEditDTO managerMemberEditDTO, List<String> tenantIds) {
        //过滤会员昵称敏感词
        if (CharSequenceUtil.isNotBlank(managerMemberEditDTO.getNickName())) {
            managerMemberEditDTO.setNickName(SensitiveWordsFilter.filter(managerMemberEditDTO.getNickName()));
        }
        //如果密码不为空则加密密码
        if (CharSequenceUtil.isNotBlank(managerMemberEditDTO.getPassword())) {
            managerMemberEditDTO.setPassword(new BCryptPasswordEncoder().encode(managerMemberEditDTO.getPassword()));
        }
        //查询会员信息
        Robot member = this.getById(managerMemberEditDTO.getId());
        //传递修改会员信息
        BeanUtil.copyProperties(managerMemberEditDTO, member);
        member.setTenantIds(CharSequenceUtil.join(",", tenantIds));
        memberTenantService.updateMemberTenantStatusByManager(managerMemberEditDTO.getId(), tenantIds);
        this.updateById(member);

        // String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_INFO_EDIT.name();
        //发送订单变更mq消息
        // rocketMQTemplate.asyncSend(destination, member, RocketmqSendCallbackBuilder.commonCallback());

        return member;
    }
    @Override
    public IPage<RobotVO> getMemberPage(RobotSearchVO memberSearchVO, PageVO page) {
        QueryWrapper<Robot> queryWrapper = Wrappers.query();

        // 用户名查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(memberSearchVO.getUsername()), "username", memberSearchVO.getUsername());

        // 昵称查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(memberSearchVO.getNickName()), "nick_name", memberSearchVO.getNickName());

        // 电话号码查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(memberSearchVO.getMobile()), "mobile", memberSearchVO.getMobile());

        // 会员状态查询(暂时无需审核用户身份，注册即正常用户)
        // queryWrapper.eq(CharSequenceUtil.isNotBlank(memberSearchVO.getDisabled()), "disabled",
        //         memberSearchVO.getDisabled().equals(SwitchEnum.OPEN.name()) ? 1 : 0);

        // 租户ID查询
        queryWrapper.like(CharSequenceUtil.isNotBlank(memberSearchVO.getTenantId()), "tenant_ids", memberSearchVO.getTenantId());

        // 按照创建时间降序排序
        queryWrapper.orderByDesc("create_time");

        queryWrapper.eq("delete_flag", false);

        Boolean applying = memberSearchVO.getApplying();
        // 获取审核申请中的用户
        if (applying != null && applying) {
            queryWrapper.isNotNull("student_id");
            queryWrapper.ne("student_id","");
        }

        // 获取所有租户列表
        List<Tenant> tenantAllList = tenantAreaService.list();

        // 执行分页查询会员信息
        IPage<Robot> memberPage = this.baseMapper.pageByMember(PageUtil.initPage(page), queryWrapper);

        List<RobotVO> result = new ArrayList<>();

        // 遍历会员信息，转换为视图对象
        memberPage.getRecords().forEach(member -> {
            RobotVO memberVO = new RobotVO(member);

            // 如果用户租户不为空，则填充租户信息
            if (!CharSequenceUtil.isEmpty(member.getTenantIds())) {
                try {
                    List<String> tenantList = Arrays.asList(member.getTenantIds().split(","));
                    // 根据租户ID过滤，留下用户所在的租户（可多个），并填充租户信息至会员视图对象
                    memberVO.setTenants(
                            tenantAllList.stream().filter(tenant -> tenantList.contains(tenant.getId()))
                                    .collect(Collectors.toList())
                    );
                } catch (Exception e) {
                    log.error("填充租户信息异常", e);
                }
            }
            result.add(memberVO);
        });

        // 构造返回的分页结果，
        Page<RobotVO> pageResult = new Page(memberPage.getCurrent(), memberPage.getSize(), memberPage.getTotal());
        // 分页结果携带会员视图对象列表返回
        pageResult.setRecords(result);

        return pageResult;
    }
    @Override
    public RobotVO getMember(String id) {
        Robot member = this.getById(id);
        System.out.println("-------------------------");
        System.out.println(member);
        System.out.println("-------------------------");
        List<Tenant> tenantAllList = tenantAreaService.list();
        RobotVO memberVO = new RobotVO(this.getById(id));
        if (!CharSequenceUtil.isEmpty(member.getTenantIds())) {
            try {
                List<String> tenantList = Arrays.asList(member.getTenantIds().split(","));
                memberVO.setTenants(
                        tenantAllList.stream().filter
                                        (tenant -> tenantList.contains(tenant.getId()))
                                .collect(Collectors.toList())
                );
            } catch (Exception e) {
                log.error("填充租户信息异常", e);
            }
        }
        return memberVO;
    }

    @Override
    public void deleteRobotById(String id) {
        robotService.removeById(id);
    }
}