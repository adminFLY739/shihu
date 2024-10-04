package cn.lili.modules.goods.serviceimpl;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.Shipments;
import cn.lili.modules.goods.mapper.ShipmentsMapper;
import cn.lili.modules.goods.service.ShipmentsService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author: nxc
 * @since: 2023/9/7 09:02
 * @description: 发货地业务实现层
 */

@Service
public class ShipmentsServiceImpl extends ServiceImpl<ShipmentsMapper, Shipments> implements ShipmentsService {
  @Override
  public IPage<Shipments> queryByParams(PageVO pageVO, String storeId) {
    QueryWrapper<Shipments> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("store_id",storeId);
    return baseMapper.queryShipmentList(PageUtil.initPage(pageVO),queryWrapper);
  }
}
