package cn.lili.modules.permission.service;


import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.security.token.Token;
import cn.lili.modules.permission.entity.dos.AdminUser;
import cn.lili.modules.permission.entity.dto.AdminUserDTO;
import cn.lili.modules.permission.entity.vo.AdminUserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

/**
 * 用户业务层
 *
 * @author Chopper
 * @since 2020/11/17 3:42 下午
 */
@CacheConfig(cacheNames = "{adminuser}")
public interface AdminUserService extends IService<AdminUser> {


    /**
     * 获取管理员分页
     *
     * @param initPage      分页条件
     * @param initWrapper   查询条件
     * @return  分页查询结果
     */
    IPage<AdminUserVO> adminUserPage(Page initPage, QueryWrapper<AdminUser> initWrapper);

    /**
     * 通过用户名获取用户
     *
     * @param username  用户
     * @return  用户
     */
    AdminUser findByUsername(String username);


    /**
     * 更新管理员
     *
     * @param adminUser   管理员
     * @param roles       权限
     * @return 更新结果
     */
    boolean updateAdminUser(AdminUser adminUser, List<String> roles);


    /**
     * 修改管理员密码
     *
     * @param password     旧密码
     * @param newPassword  新密码
     */
    void editPassword(String password, String newPassword);

    /**
     * 重置密码
     *
     * @param ids id集合
     */
    void resetPassword(List<String> ids);

    /**
     * 新增管理员
     *
     * @param adminUser  管理员
     * @param roles      权限
     */
    void saveAdminUser(AdminUserDTO adminUser, List<String> roles);

    /**
     * 彻底删除
     *
     * @param ids  管理员id
     */
    void deleteCompletely(List<String> ids);


    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    Token login(String username, String password);

    /**
     * 刷新token
     *
     * @param refreshToken  重刷令牌
     * @return token
     */
    Token refreshToken(String refreshToken);

    /**
     * 登出
     *
     * @param userEnums token角色类型
     */
    void logout(UserEnums userEnums);

}
