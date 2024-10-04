package cn.lili.modules.member.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.member.entity.dos.MemberTenant;
import cn.lili.modules.member.entity.dto.MemberTenantSearchParams;
import cn.lili.modules.member.entity.enums.MemberStatusEnum;
import cn.lili.modules.member.entity.vo.MainTenantVO;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.lettuce.core.ZAddArgs;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/7/4 09:30
 * @description: 用户租户管理业务层
 */
public interface MemberTenantService extends IService<MemberTenant> {

    /**
    *
    * 申请租户
    *
    *@Param: tenantIdList 租户id数组
    *@Param: memberId  用户id
    *@return: 申请租户
    *@Author: nxc
    *@date: 2023/7/4
    */
    boolean applyTenantId(List<String> tenantIdList, String memberId);


  /**
   *
   * 加入租户
   *
   *@Param: tenantId 租户id
   *@Param: memberId  用户id
   *@return: 申请租户
   *@Author: nxc
   *@date: 2023/7/4
   */
  boolean addTenantId(String tenantId, String memberId);

    /**
    *
    * 用户租户分页
    *
    *@Param: entity 搜索参数
    *@Param:  page 分页参数
    *@return:  用户租户分页
    *@Author: nxc
    *@date: 2023/7/4
    */
    IPage<MemberTenantVO> getByPage(MemberTenantSearchParams entity, PageVO page);

    /**
    *
    * 修改用户租户状态
    *
    *@Param: memberId 用户id
    *@Param: tenantId 租户id
    *@Param: memberStatus 用户租户状态
    *@Author: nxc
    *@date: 2023/7/4
    */
    void updateMemberTenantStatus(String memberId, String tenantId, String memberStatus);


    /**
    *
    * 管理员直接修改租户id
    *
    *@Param: memberId 用户id
    *@Param: tenantIds  租户ids
    *@Author: nxc
    *@date: 2023/7/4
    */
    void updateMemberTenantStatusByManager(String memberId , List<String> tenantIds);

    /**
     * 获取对应用户状态的用户数量
     *
     * @param memberStatusEnum 用户状态枚举
     * @param tenantId 租户Id
     * @return 所有的已上架的商品数量
     */
    long memberNum(MemberStatusEnum memberStatusEnum, String tenantId);


    /**
    *
    * 设置主租户
    *
    *@Param: tenantId 租户id
    *@Param: memberId  用户id
    *@return: 设置结果
    *@Author: nxc
    *@date: 2023/7/12
    */
    boolean setMainTenant(String tenantId, String memberId);

    /**
     *
     * 获得当前用户主租户
     *
     *@Param: appidId  小程序id
     *@return: 主租户VO
     *@Author: nxc
     *@date: 2023/7/12
     */
    MainTenantVO getMainTenant( String appid);

    /**
    *
    * 判断用户是否在店铺中
    *
    *@Param: tenantId 租户id
    *@Param: storeId  店铺id
    *@return: 判断结果
    *@Author: nxc
    *@date: 2023/8/3
    */
    boolean memberInStore(String tenantId , String storeId);
}
