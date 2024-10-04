package cn.lili.modules.goods.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.Shipments;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: nxc
 * @since: 2023/9/7 08:59
 * @description: 店铺发货业务层
 */
public interface ShipmentsService extends IService<Shipments> {

  /**
   * 商品发货地查询
   *
   * @param pageVO  分页参数
   * @param storeId 店铺id               、
   * @return 商品发货地分页
   */
  IPage<Shipments> queryByParams(PageVO pageVO,String storeId);


}
