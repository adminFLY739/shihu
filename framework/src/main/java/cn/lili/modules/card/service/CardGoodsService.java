package cn.lili.modules.card.service;

import cn.lili.modules.card.entity.dos.CardGoods;
import cn.lili.modules.card.entity.dto.CardGoodsSearchParams;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 10:45
 * @description: 卡券商品业务接口层
 */
public interface CardGoodsService extends IService<CardGoods> {


    /**
     * 删除卡券商品
     *
     * @param cardIds 卡券id
     */
    void deleteCardGoods(List<String> cardIds);

    /**
     * 获取卡券商品信息
     *
     * @param searchParams 查询参数
     * @return 促销商品列表
     */
    List<CardGoods> listFindAll(CardGoodsSearchParams searchParams);



}
