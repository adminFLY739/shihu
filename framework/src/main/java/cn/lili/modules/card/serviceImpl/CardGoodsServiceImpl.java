package cn.lili.modules.card.serviceImpl;

import cn.lili.modules.card.entity.dos.CardGoods;
import cn.lili.modules.card.entity.dto.CardGoodsSearchParams;
import cn.lili.modules.card.mapper.CardGoodsMapper;
import cn.lili.modules.card.service.CardGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 10:46
 * @description: 卡券商品业务处理层
 */

@Service
public class CardGoodsServiceImpl extends ServiceImpl<CardGoodsMapper, CardGoods> implements CardGoodsService {

    /**
     * 删除卡券商品
     *
     * @param cardIds 卡券id
     */
    @Override
    public void deleteCardGoods(List<String> cardIds) {
        LambdaQueryWrapper<CardGoods> queryWrapper = new LambdaQueryWrapper<CardGoods>().in(CardGoods::getCardId, cardIds);
        this.remove(queryWrapper);
    }

    /**
     * 获取促销商品信息
     *
     * @param searchParams 查询参数
     * @return 促销商品列表
     */
    @Override
    public List<CardGoods> listFindAll(CardGoodsSearchParams searchParams) {
        return this.list(searchParams.queryWrapper());
    }

}
