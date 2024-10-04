package cn.lili.modules.permission.service;


import cn.lili.modules.permission.entity.dos.AdminUser;
import cn.lili.modules.permission.entity.dos.Role;
import cn.lili.modules.permission.entity.vo.AdminUserVO;
import cn.lili.modules.permission.entity.vo.RoleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色业务层
 *
 * @author Chopper
 * @since 2020/11/17 3:45 下午
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取默认角色
     *
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);


    /**
     * 批量删除角色
     * @param roleIds
     */
    void deleteRoles(List<String> roleIds);

    /**
     * 获取角色分页
     *
     * @param initPage
     * @param initWrapper
     * @return
     */
    IPage<RoleVO> rolePage(Page initPage, QueryWrapper<Role> initWrapper);

}
