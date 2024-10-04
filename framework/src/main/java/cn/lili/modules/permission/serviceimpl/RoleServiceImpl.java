package cn.lili.modules.permission.serviceimpl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.permission.entity.dos.AdminUser;
import cn.lili.modules.permission.entity.dos.Department;
import cn.lili.modules.permission.entity.dos.Role;
import cn.lili.modules.permission.entity.vo.AdminUserVO;
import cn.lili.modules.permission.entity.vo.RoleVO;
import cn.lili.modules.permission.mapper.RoleMapper;
import cn.lili.modules.permission.service.DepartmentRoleService;
import cn.lili.modules.permission.service.RoleMenuService;
import cn.lili.modules.permission.service.RoleService;
import cn.lili.modules.permission.service.UserRoleService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色业务层实现
 *
 * @author Chopper
 * @since 2020/11/17 3:50 下午
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    /**
     * 部门角色
     */
    @Autowired
    private DepartmentRoleService departmentRoleService;
    /**
     * 用户权限
     */
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 租户
     */
    @Autowired
    private TenantAreaService tenantAreaService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<Role> findByDefaultRole(Boolean defaultRole) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("default_role", true);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(List<String> roleIds) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        if (departmentRoleService.count(queryWrapper) > 0) {
            throw new ServiceException(ResultCode.PERMISSION_DEPARTMENT_ROLE_ERROR);
        }
        if (userRoleService.count(queryWrapper) > 0) {
            throw new ServiceException(ResultCode.PERMISSION_USER_ROLE_ERROR);
        }
        if(roleIds.contains("1668082517050101761")){
            throw new ServiceException(ResultCode.PERMISSION_TANENT_ERROR);
        }
        //删除角色
        this.removeByIds(roleIds);
        //删除角色与菜单关联
        roleMenuService.remove(queryWrapper);
    }

    @Override
    public IPage<RoleVO> rolePage(Page initPage, QueryWrapper<Role> initWrapper) {
        Page<Role> rolePage = page(initPage, initWrapper);
        List<Tenant> tenants = tenantAreaService.list();

        List<RoleVO> result = new ArrayList<>();

        rolePage.getRecords().forEach(role -> {
            RoleVO roleVO = new RoleVO(role);
            if (!CharSequenceUtil.isEmpty(roleVO.getTenantId())) {
                try {
                    roleVO.setTenantName(
                            tenants.stream().filter
                                            (tenant -> tenant.getId().equals(roleVO.getTenantId()))
                                    .collect(Collectors.toList())
                                    .get(0)
                                    .getName()
                    );
                } catch (Exception e) {
                    log.error("填充租户信息异常", e);
                }
            }
            result.add(roleVO);
        });
        Page<RoleVO> pageResult = new Page(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        pageResult.setRecords(result);
        return pageResult;

    }
}
