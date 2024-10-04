package cn.lili.modules.card.serviceImpl;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dos.Delivery;
import cn.lili.modules.card.entity.dto.DeliveryImportDTO;
import cn.lili.modules.card.entity.dto.DeliverySearchParams;
import cn.lili.modules.card.entity.enums.deliveryStatus;
import cn.lili.modules.card.entity.vo.CardInfo;
import cn.lili.modules.card.entity.vo.CardVO;
import cn.lili.modules.card.entity.vo.DeliveryVO;
import cn.lili.modules.card.mapper.DeliveryMapper;
import cn.lili.modules.card.service.CardService;
import cn.lili.modules.card.service.DeliveryService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author: nxc
 * @since: 2023/6/19 09:22
 * @description: 提货码业务实现层
 */

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    @Autowired
    private CardService cardService;

    @Override
    public boolean createDelivery(Card card) {
        int num = card.getPublishNum();
        List<Delivery> deliveryList = new ArrayList<>();
        for(int i = 0 ; i < num ; i++){
            Delivery delivery = new Delivery();
            delivery.setCardId(card.getId());
            delivery.setDeliveryCode(SnowFlake.createDelivery("D"));
            delivery.setDeliveryPassword(StringUtils.getRandStr(6));
            delivery.setDeliveryStatus(deliveryStatus.NOTISSUED.name());
            deliveryList.add(delivery);
        }
        this.saveBatch(deliveryList);
        return false;
    }

    @Override
    public IPage<DeliveryVO> queryDelivery(DeliverySearchParams queryParam, PageVO page) {
        AuthUser user = Objects.requireNonNull(UserContext.getCurrentUser());
        queryParam.setStoreId(user.getStoreId());
        return  baseMapper.queryDelivery(PageUtil.initPage(page),queryParam.queryWrapper());
    }

    @Override
    public boolean changeDeliveryStatus(List<String> cardIds, String status) {

        UpdateWrapper<Delivery> updateWrapper = Wrappers.update();
        updateWrapper.in("card_id", cardIds);
        updateWrapper.set("delivery_status", status);
        return this.update(updateWrapper);
    }

    @Override
    public boolean saveDelivery(DeliveryImportDTO deliveryImportDTO) {
        Card card = cardService.getById(deliveryImportDTO.getCardId());
        int num = deliveryImportDTO.getPublishNum();
        List<Delivery> deliveryList = new ArrayList<>();
        for(int i = 0 ; i < num ; i++){
            Delivery delivery = new Delivery();
            delivery.setCardId(card.getId());
            delivery.setDeliveryCode(SnowFlake.createDelivery("D"));
            delivery.setDeliveryPassword(StringUtils.getRandStr(6));
            delivery.setDeliveryStatus(deliveryStatus.NOTISSUED.name());
            deliveryList.add(delivery);
        }
        cardService.updatePublishNum(deliveryImportDTO.getCardId(), deliveryImportDTO.getPublishNum());
        return this.saveBatch(deliveryList);
    }

    @Override
    public boolean changeDeliveryStauts(String deliveryIds, String status) {
        if(status.equals(deliveryStatus.NOTUSE.name())){
            List<String> deliveryIdList = Arrays.asList(deliveryIds.split(","));
            UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", deliveryIdList);
            updateWrapper.eq("delivery_status",deliveryStatus.NOTISSUED.name());
            updateWrapper.set("delivery_status", status);
            return this.update(updateWrapper);
        }
        else {
            List<String> deliveryIdList = Arrays.asList(deliveryIds.split(","));
            UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", deliveryIdList);
            updateWrapper.set("delivery_status", status);
            return this.update(updateWrapper);
        }
    }

    @Override
    public boolean cardAuth(String deliveryCode, String deliveryPassword) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delivery_code", deliveryCode);
        Delivery delivery = this.getOne(queryWrapper);
        if(delivery==null){
            throw new ServiceException(ResultCode.DELIVERY_GET_ERROR);
        }
        if(!deliveryPassword.equals(delivery.getDeliveryPassword())){
            throw new ServiceException(ResultCode.DELIVERY_PASSWORD_ERROR);
        }
        if(!delivery.getDeliveryStatus().equals(deliveryStatus.NOTUSE.name())&&!delivery.getDeliveryStatus().equals(deliveryStatus.RECEIVED.name())){
            throw new ServiceException(ResultCode.DELIVERY_STATUS_AUTH_ERROR);
        }


        return true;
    }

    @Override
    public CardInfo getCardInfo(String deliveryCode) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delivery_code", deliveryCode);
        Delivery delivery = this.getOne(queryWrapper);
        CardVO cardVO = cardService.getDetail(delivery.getCardId());
        CardInfo cardInfo = new CardInfo(cardVO);
        cardInfo.setDeliveryStatus(delivery.getDeliveryStatus());
        cardInfo.setDeliveryId(delivery.getId());
        return cardInfo;

    }

    @Override
    public long getOverNum(String id) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_id",id);
        queryWrapper.eq("delivery_status",deliveryStatus.NOTISSUED.name());
        return this.count(queryWrapper);
    }

    @Override
    public Delivery getOneByCardId(String cardId) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_id",cardId);
        queryWrapper.eq("delivery_status",deliveryStatus.NOTISSUED.name());
        List<Delivery> deliveryList = this.list(queryWrapper);
        if(deliveryList.size()>0){
            return deliveryList.get(0);
        }
        else{
            return null;
        }
    }

    @Override
    public void updateMember(String memberId, String id) {
        UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("member_id", memberId);
        this.update(updateWrapper);
    }

}
