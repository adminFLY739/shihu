package cn.lili.modules.member.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dos.MemberTenant;
import cn.lili.modules.member.entity.dto.MemberTenantSearchParams;
import cn.lili.modules.member.entity.enums.MemberStatusEnum;
import cn.lili.modules.member.entity.vo.MainTenantVO;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import cn.lili.modules.member.mapper.MemberTenantMapper;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.member.service.MemberTenantService;
import cn.lili.modules.store.entity.dos.StoreTenant;
import cn.lili.modules.store.service.StoreTenantService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: nxc
 * @since: 2023/7/4 09:31
 * @description:
 */

@Service
public class MemberTenantServiceImpl extends ServiceImpl<MemberTenantMapper, MemberTenant> implements MemberTenantService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreTenantService storeTenantService;

    @Autowired
    private TenantAreaService tenantAreaService;

    @Override
    public boolean applyTenantId(List<String> tenantIdList, String memberId) {

        tenantIdList.forEach(tenantId -> {
            MemberTenant memberTenant = this.getMemberTenant(tenantId, memberId);
            if (memberTenant != null) {
                if (memberTenant.getMemberStatus().equals(MemberStatusEnum.APPLYING.name()) || memberTenant.getMemberStatus().equals(MemberStatusEnum.OPEN.name())) {
                    throw new ServiceException(ResultCode.APPLY_MEMBER_TENANT_REPEAT);
                }
            }

            if (memberTenant != null) {
                memberTenant.setMemberStatus(MemberStatusEnum.APPLYING.name());
                this.updateById(memberTenant);
            } else {
                memberTenant = new MemberTenant();
                memberTenant.setMemberId(memberId);
                memberTenant.setTenantId(tenantId);
                memberTenant.setMemberStatus(MemberStatusEnum.APPLYING.name());
                this.save(memberTenant);
            }
        });
        return true;
    }

    @Override
    public boolean addTenantId(String tenantId, String memberId) {
        MemberTenant memberTenant = this.getMemberTenant(tenantId, memberId);

        if (memberTenant != null) {
            return false;
        }

        memberTenant = new MemberTenant();
        memberTenant.setMemberId(memberId);
        memberTenant.setTenantId(tenantId);
        memberTenant.setMemberStatus(MemberStatusEnum.OPEN.name());
        // 给租户添加用户
        this.save(memberTenant);
        // 给用户绑定租户
        this.updateMemberTenant(memberId);
        return true;
    }


    @Override
    public IPage<MemberTenantVO> getByPage(MemberTenantSearchParams entity, PageVO page) {
        return this.baseMapper.getByPage(PageUtil.initPage(page), entity.queryWrapper());
    }

    @Override
    public void updateMemberTenantStatus(String memberId, String tenantId, String memberStatus) {
        MemberTenant memberTenant = this.getMemberTenant(tenantId, memberId);
        memberTenant.setMemberStatus(memberStatus);
        //更新li_member_Tenant
        this.updateById(memberTenant);
        //
        this.updateMemberTenant(memberId);
    }

    @Override
    public void updateMemberTenantStatusByManager(String memberId, List<String> tenantIds) {
        QueryWrapper<MemberTenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        this.remove(queryWrapper);
        tenantIds.forEach(tenantId -> {
            MemberTenant memberTenant = new MemberTenant();
            memberTenant.setMemberId(memberId);
            memberTenant.setTenantId(tenantId);
            memberTenant.setMemberStatus(MemberStatusEnum.OPEN.name());
            this.save(memberTenant);
        });

    }

    @Override
    public long memberNum(MemberStatusEnum memberStatusEnum, String tenantId) {
        QueryWrapper<MemberTenant> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(memberStatusEnum.name())) {
            queryWrapper.eq("member_status", memberStatusEnum.name());
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        return this.count(queryWrapper);
    }

    @Override
    public boolean setMainTenant(String tenantId, String memberId) {
        UpdateWrapper<MemberTenant> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("member_id", memberId);
        updateWrapper.set("main_tenant", false);
        this.update(updateWrapper);
        updateWrapper.eq("member_id", memberId);
        updateWrapper.eq("tenant_id", tenantId);
        updateWrapper.set("main_tenant", true);
        return this.update(updateWrapper);
    }

    @Override
    public MainTenantVO getMainTenant(String appid) {
        Tenant tenant = tenantAreaService.getTenantByAppId(appid);
        return new MainTenantVO(tenant);
    }

    @Override
    public boolean memberInStore(String tenantId, String storeId) {
        QueryWrapper<StoreTenant> queryWrapperStore = new QueryWrapper<>();
        queryWrapperStore.eq("store_id", storeId);
        List<StoreTenant> storeTenantList = storeTenantService.list(queryWrapperStore);
        for (StoreTenant storeTenant : storeTenantList) {
            if (tenantId.equals(storeTenant.getTenantId())) {
                return true;
            }
        }
        return false;
    }

    public void updateMemberTenant(String memberId) {
        // 创建查询条件
        QueryWrapper<MemberTenant> queryWrapper = new QueryWrapper<>();
        // 会员状态为开启状态
        queryWrapper.eq("member_status", MemberStatusEnum.OPEN.name());
        // 会员ID
        queryWrapper.eq("member_id", memberId);

        // 执行查询
        List<MemberTenant> memberTenantList = this.list(queryWrapper);

        // 从查询结果中提取租户ID列表
        List<String> tenantList = memberTenantList.stream().map(MemberTenant::getTenantId).collect(Collectors.toList());

        // 根据会员ID获取会员信息
        Member member = memberService.getById(memberId);

        // 将租户ID列表转换为逗号分隔的字符串，并设置到会员对象中(更新用户的租户ids，如果启用则增加，禁用则减少)
        member.setTenantIds(CharSequenceUtil.join(",", tenantList));

        // 更新会员信息
        memberService.updateById(member);
    }


    public MemberTenant getMemberTenant(String tenantId, String memberId) {
        QueryWrapper<MemberTenant> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("member_id", memberId);
        }
        return this.getOne(queryWrapper);
    }

}
