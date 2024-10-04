package cn.lili.modules.member.mapper;

import cn.lili.modules.member.entity.dos.MemberTenant;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import cn.lili.modules.store.entity.vos.StoreVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: nxc
 * @since: 2023/7/4 09:31
 * @description: 用户租户数据管理层
 */
public interface MemberTenantMapper extends BaseMapper<MemberTenant> {

    /**
     * 获取用户租户
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 店铺VO分页列表
     */
    @Select("select me.* , name , m.mobile , m.nick_name , m.username from li_member_tenant as me join tenant as t on me.tenant_id = t.id join li_member as m on me.member_id = m.id ${ew.customSqlSegment}")
    IPage<MemberTenantVO> getByPage(IPage<MemberTenantVO> page, @Param(Constants.WRAPPER) Wrapper<MemberTenantVO> queryWrapper);
}
