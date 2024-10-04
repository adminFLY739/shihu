package cn.lili.modules.member.serviceimpl;

import cn.lili.modules.member.entity.dos.ShipmentsClerk;
import cn.lili.modules.member.entity.vo.ClerkVO;
import cn.lili.modules.member.mapper.ShipmentsClerkMapper;
import cn.lili.modules.member.service.ShipmentsClerkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/9/7 10:12
 * @description: 发货地店员业务实现层
 */

@Service
public class ShipmentsClerkServiceImpl extends ServiceImpl<ShipmentsClerkMapper, ShipmentsClerk> implements ShipmentsClerkService {
    @Override
    public void addShipmentsClerk(List<String> ids, String shipmentsId) {
      //先把所有的删除，在进行添加
      QueryWrapper<ShipmentsClerk> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("shipments_id",shipmentsId);
      this.remove(queryWrapper);
      for (String id : ids) {
        ShipmentsClerk shipmentsClerk = new ShipmentsClerk();
        shipmentsClerk.setShipmentsId(shipmentsId);
        shipmentsClerk.setClerkId(id);
        this.save(shipmentsClerk);
      }
    }

  @Override
  public List<ClerkVO> getShipmentsClerk(String shipmentsId) {
      return baseMapper.getClerkList(shipmentsId);
  }

  @Override
  public boolean isInShipments(String clerkId) {
      QueryWrapper<ShipmentsClerk> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("clerk_id",clerkId);
      return !this.list(queryWrapper).isEmpty();
  }
}
