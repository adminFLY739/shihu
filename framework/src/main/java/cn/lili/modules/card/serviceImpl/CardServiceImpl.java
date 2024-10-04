package cn.lili.modules.card.serviceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.cache.Cache;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.CurrencyUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.CardGoods;
import cn.lili.modules.card.entity.dos.Delivery;
import cn.lili.modules.card.entity.dto.CardGoodsSearchParams;
import cn.lili.modules.card.entity.dto.CardImportDTO;
import cn.lili.modules.card.entity.dto.CardSearchParams;
import cn.lili.modules.card.entity.enums.deliveryStatus;
import cn.lili.modules.card.entity.vo.CardVO;
import cn.lili.modules.card.mapper.CardMapper;
import cn.lili.modules.card.service.CardGoodsService;
import cn.lili.modules.card.service.CardService;
import cn.lili.modules.card.service.DeliveryService;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.dos.Wholesale;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsSalesModeEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.goods.service.WholesaleService;
import cn.lili.modules.member.service.MemberAddressService;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.enums.CartTypeEnum;
import cn.lili.modules.order.cart.entity.vo.CartSkuVO;
import cn.lili.modules.promotion.tools.PromotionTools;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: nxc
 * @since: 2023/6/17 09:57
 * @description: 卡券业务实现层
 */

@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    static String errorMessage = "卡券提货异常，请稍后重试";

    @Autowired
    private CardGoodsService cardGoodsService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private WholesaleService wholesaleService;


    /**
     * 缓存
     */
    @Autowired
    private Cache<Object> cache;


    @Override
    public IPage<CardVO> queryCard(CardSearchParams queryParam, PageVO page) {

        AuthUser user = Objects.requireNonNull(UserContext.getCurrentUser());
        queryParam.setStoreId(user.getStoreId());
        IPage<Card> cardIPage  = baseMapper.queryCard(PageUtil.initPage(page),queryParam.queryWrapper());
        List<CardVO> cardVOList = cardIPage.getRecords().stream().map(CardVO::new).collect(Collectors.toList());
        return PageUtil.convertPage(cardIPage, cardVOList);
    }

    @Override
    public List<Card> getStoreAllCardList(String storeId) {
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Card::getStoreId, storeId);
        lambdaQueryWrapper.ge(Card::getEndTime, new Date());
        return this.list(lambdaQueryWrapper);
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean  saveCard(CardImportDTO cardImportDTO){
        cardImportDTO.setUsedNum(0);
        cardImportDTO.setReceivedNum(0);
        this.checkPromotions(cardImportDTO);
        boolean save = this.save(cardImportDTO);
        this.updateCardGoods(cardImportDTO);
        deliveryService.createDelivery(cardImportDTO);
        return save;
    }


    /**
     * 获取卡券展示详情
     *
     * @param cardId 卡券id
     * @return 返回卡券展示详情
     */
    @Override
    public CardVO getDetail(String cardId) {
        CardVO cardVO = new CardVO(this.getById(cardId));
        CardGoodsSearchParams searchParams = new CardGoodsSearchParams();
        searchParams.setCardId(cardId);
        List<CardGoods> cardGoodsByCardId = this.cardGoodsService.listFindAll(searchParams);
        if (cardGoodsByCardId != null && !cardGoodsByCardId.isEmpty()) {
            cardVO.setCardGoodsList(cardGoodsByCardId);
        }
        cardVO.setOverNum(deliveryService.getOverNum(cardId));
        return cardVO;
    }

    /**
     * 卡券更新
     *
     * @param cardVO 卡券信息
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCard(CardVO cardVO) {
        Card card = this.getById(cardVO.getId());
        if (card  == null) {
            throw new ServiceException(ResultCode. CARD_GET_ERROR);
        }
        PromotionTools.checkPromotionTime( cardVO.getStartTime(),  cardVO.getEndTime());
        boolean save = this.updateById(cardVO);
        this.updateCardGoods(cardVO);
        return save;
    }


    /**
     * 更新卡券状态
     * 如果要更新卡券状态为关闭，startTime和endTime置为空即可
     *
     * @param ids       促销id集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateStatus(List<String> ids, Long startTime, Long endTime) {

        List<Card> cardList = this.list(new QueryWrapper<Card>().in("id", ids));
        for (Card card : cardList) {
            if (startTime != null && endTime != null) {
                card.setStartTime(new Date(startTime));
                card.setEndTime(new Date(endTime));
            } else {
                card.setStartTime(null);
                card.setEndTime(null);
            }
            if (card.getStartTime() == null && card.getEndTime() == null) {
                this.cardGoodsService.deleteCardGoods(Collections.singletonList(card.getId()));
            }

        }
        //关闭卡券的同时关闭相关提货码
        deliveryService.changeDeliveryStatus(ids, deliveryStatus.CLOSED.name());
        if (startTime != null && endTime != null) {
            return this.update(new UpdateWrapper<Card>().in("id", ids).set("start_time", new Date(startTime)).set("end_time", new Date(endTime)));
        } else {
            return this.update(new UpdateWrapper<Card>().in("id", ids).set("start_time", null).set("end_time", null));
        }
    }

    @Override
    public boolean updatePublishNum(String cardId, Integer num) {
        Card card = this.getById(cardId);
        return this.update(new UpdateWrapper<Card>().eq("id",cardId).set("publish_num",card.getPublishNum()+num));
    }





    /**
     * 检查卡券参数
     *
     * @param cardImportDTO 卡券添加dto
     */
    public void checkPromotions(CardImportDTO cardImportDTO) {
        PromotionTools.checkPromotionTime(cardImportDTO.getStartTime(), cardImportDTO.getEndTime());
    }

    /**
     * 更新卡券商品信息
     *
     * @param cardImportDTO 卡券添加dto
     */
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateCardGoods(CardImportDTO cardImportDTO) {
        this.cardGoodsService.deleteCardGoods(Collections.singletonList(cardImportDTO.getId()));
        List<CardGoods> cardGoodsList = cardGoodsInit(cardImportDTO.getCardGoodsList(), cardImportDTO);
        for (CardGoods cardGoods : cardGoodsList) {
            cardGoods.setStoreId(cardImportDTO.getStoreId());
            cardGoods.setStoreName(cardImportDTO.getStoreName());
        }
        //促销活动商品更新
        return cardGoodsService.saveBatch(cardGoodsList);
    }
    /**
     * 更新卡券商品信息
     *
     * @param cardVO 卡券vo
     */
    @Transactional(rollbackFor = {Exception.class})
    public boolean updateCardGoods(CardVO cardVO) {
        this.cardGoodsService.deleteCardGoods(Collections.singletonList(cardVO.getId()));
        List<CardGoods> cardGoodsList = cardGoodsInit(cardVO.getCardGoodsList(), cardVO);
        for (CardGoods cardGoods : cardGoodsList) {
            cardGoods.setStoreId(cardVO.getStoreId());
            cardGoods.setStoreName(cardVO.getStoreName());
        }
        //促销活动商品更新
        return cardGoodsService.saveBatch(cardGoodsList);
    }
    /**
     * 促销商品入库前填充
     *
     * @param originList 原促销商品列表
     * @param cardImportDTO  促销信息
     * @return 促销商品列表
     */
    public List<CardGoods> cardGoodsInit(List<CardGoods> originList,CardImportDTO cardImportDTO) {
        if (originList != null) {
            //本次促销商品入库
            for (CardGoods cardGoods : originList) {
                cardGoods.setCardId(cardImportDTO.getId());
                if (CharSequenceUtil.isEmpty(cardGoods.getStoreId())) {
                    cardGoods.setStoreId(cardImportDTO.getStoreId());
                }
                if (CharSequenceUtil.isEmpty(cardGoods.getStoreName())) {
                    cardGoods.setStoreName(cardImportDTO.getStoreName());
                }
                cardGoods.setTitle(cardImportDTO.getPromotionName());
                // 如果是秒杀活动保留原时间
                if (cardGoods.getStartTime() == null ) {
                    cardGoods.setStartTime(cardImportDTO.getStartTime());
                }
                if (cardGoods.getStartTime() == null ) {
                    cardGoods.setEndTime(cardImportDTO.getEndTime());
                }
                cardGoods.setNum(0);
                cardGoods.setDeleteFlag(cardImportDTO.getDeleteFlag());
            }
        }
        return originList;
    }
    /**
     * 促销商品入库前填充
     *
     * @param originList 原促销商品列表
     * @param cardImportDTO  促销信息
     * @return 促销商品列表
     */
    public List<CardGoods> cardGoodsInit(List<CardGoods> originList,CardVO cardImportDTO) {
        if (originList != null) {
            //本次促销商品入库
            for (CardGoods cardGoods : originList) {
                cardGoods.setCardId(cardImportDTO.getId());
                if (CharSequenceUtil.isEmpty(cardGoods.getStoreId())) {
                    cardGoods.setStoreId(cardImportDTO.getStoreId());
                }
                if (CharSequenceUtil.isEmpty(cardGoods.getStoreName())) {
                    cardGoods.setStoreName(cardImportDTO.getStoreName());
                }
                cardGoods.setTitle(cardImportDTO.getPromotionName());
                // 如果是秒杀活动保留原时间
                if (cardGoods.getStartTime() == null ) {
                    cardGoods.setStartTime(cardImportDTO.getStartTime());
                }
                if (cardGoods.getStartTime() == null ) {
                    cardGoods.setEndTime(cardImportDTO.getEndTime());
                }
                cardGoods.setNum(0);
                cardGoods.setDeleteFlag(cardImportDTO.getDeleteFlag());
            }
        }
        return originList;
    }

    @Override
    public void add(String skuId, Integer num, String cartType, Boolean cover,String tenantId) {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        if (num <= 0) {
            throw new ServiceException(ResultCode.CART_NUM_ERROR);
        }
        CartTypeEnum cartTypeEnum = CartTypeEnum.CARD;;
        GoodsSku dataSku = checkGoods(skuId);

        try {
            //购物车方式购买需要保存之前的选择，其他方式购买，则直接抹除掉之前的记录
            TradeDTO tradeDTO;

        //如果存在，则变更数量不做新增，否则新增一个商品进入集合
        tradeDTO = this.readDTO(cartTypeEnum,tenantId);
        List<CartSkuVO> cartSkuVOS = tradeDTO.getSkuList();
        CartSkuVO cartSkuVO = cartSkuVOS.stream().filter(i -> i.getGoodsSku().getId().equals(skuId)).findFirst().orElse(null);


        //购物车中已经存在，更新数量
        if (cartSkuVO != null && dataSku.getCreateTime().equals(cartSkuVO.getGoodsSku().getCreateTime())) {

            //如果覆盖购物车中商品数量
            if (Boolean.TRUE.equals(cover)) {
                cartSkuVO.setNum(num);
                this.checkSetGoodsQuantity(cartSkuVO, skuId, num);
            } else {
                int oldNum = cartSkuVO.getNum();
                int newNum = oldNum + num;
                this.checkSetGoodsQuantity(cartSkuVO, skuId, newNum);
            }
            //计算购物车小计
            cartSkuVO.setSubTotal(CurrencyUtil.mul(cartSkuVO.getPurchasePrice(), cartSkuVO.getNum()));
        } else {

            //先清理一下 如果商品无效的话
            cartSkuVOS.remove(cartSkuVO);
            //购物车中不存在此商品，则新建立一个
            cartSkuVO = new CartSkuVO(dataSku, null);

            cartSkuVO.setCartType(cartTypeEnum);
            //再设置加入购物车的数量
            this.checkSetGoodsQuantity(cartSkuVO, skuId, num);
            //计算购物车小计
            cartSkuVO.setSubTotal(CurrencyUtil.mul(cartSkuVO.getPurchasePrice(), cartSkuVO.getNum()));
            cartSkuVOS.add(cartSkuVO);

                //新加入的商品都是选中的
                cartSkuVO.setChecked(true);
            }
            this.checkGoodsSaleModel(dataSku, tradeDTO.getSkuList());
            tradeDTO.setCartTypeEnum(cartTypeEnum);

            this.resetTradeDTO(tradeDTO);
        } catch (ServiceException serviceException) {
            throw serviceException;
        } catch (Exception e) {
            log.error("购物车渲染异常", e);
            throw new ServiceException(errorMessage);
        }
    }


    @Override
    public void clean(String tenantId) {
        cache.remove(this.getOriginKey(CartTypeEnum.CARD,tenantId));
    }

    @Override
    public boolean useCard(String id) {
        Delivery delivery = this.deliveryService.getById(id);
        deliveryService.changeDeliveryStauts(id,deliveryStatus.RECEIVED.name());
        Card card = this.getById(delivery.getCardId());
        card.setUsedNum(card.getUsedNum()+1);
        return this.updateById(card);
    }

    /**
     * 校验商品有效性，判定失效和库存，促销活动价格
     *
     * @param skuId 商品skuId
     */
    private GoodsSku checkGoods(String skuId) {
        GoodsSku dataSku = this.goodsSkuService.getGoodsSkuByIdFromCache(skuId);
        if (dataSku == null) {
            throw new ServiceException(ResultCode.GOODS_NOT_EXIST);
        }
        if (!GoodsAuthEnum.PASS.name().equals(dataSku.getAuthFlag()) || !GoodsStatusEnum.UPPER.name().equals(dataSku.getMarketEnable())) {
            throw new ServiceException(ResultCode.GOODS_NOT_EXIST);
        }
        return dataSku;
    }

    public TradeDTO readDTO(CartTypeEnum checkedWay,String tenantId) {
        TradeDTO tradeDTO = (TradeDTO) cache.get(this.getOriginKey(checkedWay,tenantId));
        if (tradeDTO == null) {
            tradeDTO = new TradeDTO(checkedWay);
            AuthUser currentUser = UserContext.getCurrentUser();
            tradeDTO.setMemberId(currentUser.getId());
            tradeDTO.setMemberName(currentUser.getUsername());
            tradeDTO.setTenantId(tenantId);
        }
        if (tradeDTO.getMemberAddress() == null) {
            tradeDTO.setMemberAddress(this.memberAddressService.getDefaultMemberAddress());
        }
        return tradeDTO;
    }

    /**
     * 读取当前会员购物原始数据key
     *
     * @param cartTypeEnum 获取方式
     * @param tenantId     租户id
     * @return 当前会员购物原始数据key
     */
    private String getOriginKey(CartTypeEnum cartTypeEnum,String tenantId) {

        //缓存key，默认使用购物车
        if (cartTypeEnum != null) {
            AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
            return cartTypeEnum.getPrefix() + currentUser.getId()+tenantId;
        }
        throw new ServiceException(ResultCode.ERROR);
    }

    /**
     * 检查并设置购物车商品数量
     *
     * @param cartSkuVO 购物车商品对象
     * @param skuId     商品id
     * @param num       购买数量
     */
    private void checkSetGoodsQuantity(CartSkuVO cartSkuVO, String skuId, Integer num) {
        Integer enableStock = goodsSkuService.getStock(skuId);

        //如果sku的可用库存小于等于0或者小于用户购买的数量，则不允许购买
        if (enableStock <= 0 || enableStock < num) {
            throw new ServiceException(ResultCode.GOODS_SKU_QUANTITY_NOT_ENOUGH);
        }

        if (enableStock <= num) {
            cartSkuVO.setNum(enableStock);
        } else {
            cartSkuVO.setNum(num);
        }

        if (cartSkuVO.getGoodsSku() != null && !GoodsSalesModeEnum.WHOLESALE.name().equals(cartSkuVO.getGoodsSku().getSalesModel()) && cartSkuVO.getNum() > 99) {
            cartSkuVO.setNum(99);
        }
    }

    private void checkGoodsSaleModel(GoodsSku dataSku, List<CartSkuVO> cartSkuVOS) {
        if (dataSku.getSalesModel().equals(GoodsSalesModeEnum.WHOLESALE.name())) {
            int numSum = 0;
            List<CartSkuVO> sameGoodsIdSkuList = cartSkuVOS.stream().filter(i -> i.getGoodsSku().getGoodsId().equals(dataSku.getGoodsId())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(sameGoodsIdSkuList)) {
                numSum += sameGoodsIdSkuList.stream().mapToInt(CartSkuVO::getNum).sum();
            }
            Wholesale match = wholesaleService.match(dataSku.getGoodsId(), numSum);
            if (match != null) {
                sameGoodsIdSkuList.forEach(i -> {
                    i.setPurchasePrice(match.getPrice());
                    i.setSubTotal(CurrencyUtil.mul(i.getPurchasePrice(), i.getNum()));
                });
            }
        }
    }


    public void resetTradeDTO(TradeDTO tradeDTO) {
        cache.put(this.getOriginKey(tradeDTO.getCartTypeEnum(),tradeDTO.getTenantId()), tradeDTO);
    }


}
