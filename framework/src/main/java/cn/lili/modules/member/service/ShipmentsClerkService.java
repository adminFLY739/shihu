package cn.lili.modules.member.service;

import cn.lili.modules.member.entity.dos.ShipmentsClerk;
import cn.lili.modules.member.entity.vo.ClerkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/9/7 10:11
 * @description: 发货地店员业务层
 */
public interface ShipmentsClerkService extends IService<ShipmentsClerk> {

  /**
  *
  *  添加发货地店员
  *
  *@Param: ids  店员ids
  *@Param: shipmentsId  发货地id
  *@return: 添加发货地id
  *@Author: nxc
  *@date: 2023/9/7
  */
   void addShipmentsClerk(List<String> ids, String shipmentsId);

  /**
   *  发货地店员
   * @param shipmentsId 发货地id
   * @return  该发货地店员
   */
  List<ClerkVO> getShipmentsClerk(String shipmentsId);

  /**
   *
   * @param clerkId  店员id
   * @return 是否有发货地
   */
  boolean isInShipments(String clerkId);


}
