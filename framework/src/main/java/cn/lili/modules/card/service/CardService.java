package cn.lili.modules.card.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dto.CardImportDTO;
import cn.lili.modules.card.entity.dto.CardSearchParams;
import cn.lili.modules.card.entity.vo.CardVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/17 09:55
 * @description: 卡券业务接口层
 */
public interface CardService extends IService<Card> {

    /**
     * 添加卡券
     *
     * @param cardImportDTO 添加卡券信息
     * @return 是否保存成功
     */
    boolean saveCard(CardImportDTO cardImportDTO);

    /**
     * 查询卡券
     *
     * @param page         分页
     * @param queryParam 查询条件
     * @return 卡券分页
     */
    IPage<CardVO> queryCard(CardSearchParams queryParam, PageVO page);


    /**
     *
     * 获取当前店铺所属卡券
     *
     *@param storeId 店铺id
     *@return: 租户列表
     *@Author: nxc
     *@date: 2023/6/15
     */
    List<Card> getStoreAllCardList(String storeId);

    /**
     * 获取卡券展示详情
     *
     * @param cardId 优惠券id
     * @return 返回卡券展示详情
     */
    CardVO getDetail(String cardId);

    /**
     * 通用卡券更新
     *
     * @param cardVO 卡券信息
     * @return 是否更新成功
     */
    boolean updateCard(CardVO cardVO);


    /**
     * 更新促销状态
     * 如果要更新促销状态为关闭，startTime和endTime置为空即可
     *
     * @param ids       卡券id集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 是否更新成功
     */
    boolean updateStatus(List<String> ids, Long startTime, Long endTime);

    /**
    *
    * 修改卡券发行数量
    *
    *@Param: cardId 卡券id
    *@Param: num  修改的数量
    *@return: 修改结果
    *@Author: nxc
    *@date: 2023/6/20
    */
    boolean updatePublishNum(String cardId , Integer  num);



    /**
     * 卡券购物车加入一个商品
     *
     * @param skuId    要写入的skuId
     * @param num      要加入购物车的数量
     * @param cartType 购物车类型
     * @param cover    是否覆盖购物车的数量，如果为否则累加，否则直接覆盖
     * @param tenantId 租户id
     */
    void add(String skuId, Integer num, String cartType, Boolean cover,String tenantId);


    /**
     * 清空购物车
     *
     * @param tenantId 租户id
     */
    void clean(String tenantId);


    /**
    *
    * 使用卡券
    *
    *@Param: 提货码id
    *@return: 提货结果
    *@Author: nxc
    *@date: 2023/6/21
    */
    boolean useCard(String id);





}
