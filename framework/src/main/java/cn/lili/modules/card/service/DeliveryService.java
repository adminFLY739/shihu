package cn.lili.modules.card.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.Delivery;
import cn.lili.modules.card.entity.dto.DeliveryImportDTO;
import cn.lili.modules.card.entity.dto.DeliverySearchParams;
import cn.lili.modules.card.entity.vo.CardInfo;
import cn.lili.modules.card.entity.vo.DeliveryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: nxc
 * @since: 2023/6/19 09:21
 * @description: 提货码业务接口层
 */
public interface DeliveryService extends IService<Delivery> {


    /**
    *
    * 提货码生成
    *
    *@Param: card 卡券
    *@return: 提货码生成结果
    *@Author: nxc
    *@date: 2023/6/19
    */
    boolean createDelivery(Card card);


    /**
     * 查询提货码
     *
     * @param page         分页
     * @param queryParam 查询条件
     * @return 卡券分页
     */
    IPage<DeliveryVO> queryDelivery(DeliverySearchParams queryParam, PageVO page);


    /**
    *
    * 修改提货码状态
    *
    *@Param:  cardId 卡券id
    *@Param:  status 要修改的状态
    *@return: 修改后结果
    *@Author: nxc
    *@date: 2023/6/19
    */
    boolean changeDeliveryStatus(List<String> cardIds , String status);


    boolean saveDelivery(DeliveryImportDTO deliveryImportDTO);


    /**
    *
    * 修改提货码状态
    *
    *@Param: deliveryIds 提货码ids
    *@Param: status  要修改的状态
    *@return: 修改的结果
    *@Author: nxc
    *@date: 2023/6/19
    */
    boolean changeDeliveryStauts(String deliveryIds, String status);



    /**
    *
    * 卡券提货认证
    *
    *@Param: deliveryCode 卡券提货码
    *@Param: deliveryPassoword  卡券提货密码
    *@return: 认证结果
    *@Author: nxc
    *@date: 2023/6/20
    */
    boolean cardAuth(String deliveryCode,String deliveryPassword);


    /**
    *
    * 根据提货码获取卡券信息
    *
    *@Param:
    *@return:
    *@Author: nxc
    *@date: 2023/6/20
    */
    CardInfo getCardInfo(String deliveryCode);

    /**
    *
    * 库存数量
    *
    *@Param: 卡券id
    *@return: 库存数量
    *@Author: nxc
    *@date: 2023/6/27
    */
    long getOverNum(String id);

    /**
    *
    * 获得一个提货码
    *
    *@Param: cardId 卡券id
    *@return: 提货码
    *@Author: nxc
    *@date: 2023/6/27
    */
    Delivery getOneByCardId(String cardId);


    /**
    *
    *  修改提货码所属用户
    *
    *@Param: memberId 用户id
    *@Param: id 提货码id
    *@return: 修改结果
    *@Author: nxc
    *@date: 2023/6/27
    */
    void updateMember(String memberId,String id);


}
