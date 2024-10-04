package cn.lili.modules.member.mapper;

import cn.lili.modules.member.entity.dos.ShipmentsClerk;
import cn.lili.modules.member.entity.vo.ClerkVO;
import cn.lili.modules.member.entity.vo.MemberTenantVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/9/7 10:09
 * @description: 发货地店员数据处理层
 */
public interface ShipmentsClerkMapper extends BaseMapper<ShipmentsClerk> {


  /**
   * 获取发货地店员
   *
   * @param shipmentsId 发货底id
   * @return 店铺VO分页列表
   */
  @Select("select cl.* from li_shipments_clerk as sc left join li_clerk as cl on sc.clerk_id = cl.id where sc.shipments_id = #{shipmentsId}")
  List<ClerkVO> getClerkList(String shipmentsId);

}
