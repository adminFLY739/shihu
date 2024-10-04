package cn.lili.controller.card;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.OperationalJudgment;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.card.entity.dos.Card;
import cn.lili.modules.card.entity.dto.CardImportDTO;
import cn.lili.modules.card.entity.dto.CardSearchParams;
import cn.lili.modules.card.entity.vo.CardVO;
import cn.lili.modules.card.service.CardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: nxc
 * @since: 2023/6/17 09:59
 * @description: 卡券控制层
 */

@RestController
@Api(tags = "店铺端,卡券接口")
@RequestMapping("/store/card/card")
public class CardStoreController {

    @Autowired
    private CardService cardService;


    @GetMapping
    @ApiOperation(value = "分页获取卡券列表")
    public ResultMessage<IPage<CardVO>> getCardList(CardSearchParams queryParam, PageVO page) {
        IPage<CardVO> cards = cardService.queryCard(queryParam, page);
        return ResultUtil.data(cards);
    }

    @ApiOperation(value = "获取相关店铺所有卡券")
    @GetMapping("/all")
    public ResultMessage<List<Card>> getStoreAllCardList() {
        AuthUser tokenUser = UserContext.getCurrentUser();
        if (tokenUser == null || CharSequenceUtil.isEmpty(tokenUser.getStoreId())) {
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        return ResultUtil.data(cardService.getStoreAllCardList(tokenUser.getStoreId()));
    }




    @ApiOperation(value = "添加卡券")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResultMessage<CardImportDTO> addCard(@RequestBody CardImportDTO cardImportDTO) {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        cardImportDTO.setStoreId(currentUser.getStoreId());
        cardImportDTO.setStoreName(currentUser.getStoreName());
        if (cardService.saveCard(cardImportDTO)) {
            return ResultUtil.data(cardImportDTO);
        }
        return ResultUtil.error(ResultCode.CARD_SAVE_ERROR);
    }

    @ApiOperation(value = "获取卡券详情")
    @GetMapping("/{cardId}")
    public ResultMessage<CardVO> getCard(@PathVariable String cardId) {
        CardVO card = OperationalJudgment.judgment(cardService.getDetail(cardId));
        return ResultUtil.data(card);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "修改卡券")
    public ResultMessage<CardVO> updateCard(@RequestBody CardVO cardVO) {
        OperationalJudgment.judgment(cardService.getById(cardVO.getId()));
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        cardVO.setStoreId(currentUser.getStoreId());
        cardVO.setStoreName(currentUser.getStoreName());
        if (cardService.updateCard(cardVO)) {
            return ResultUtil.data(cardVO);
        }
        return ResultUtil.error(ResultCode.CARD_SAVE_ERROR);
    }

    @ApiOperation(value = "修改卡券状态")
    @PutMapping("/status")
    public ResultMessage<Object> updateCardStatus(String cardIds, Long startTime, Long endTime) {
        AuthUser currentUser = Objects.requireNonNull(UserContext.getCurrentUser());
        String[] split = cardIds.split(",");
        List<String> cardIdList = cardService.list(new LambdaQueryWrapper<Card>().in(Card::getId, Arrays.asList(split)).eq(Card::getStoreId, currentUser.getStoreId())).stream().map(Card::getId).collect(Collectors.toList());
        if (cardService.updateStatus(cardIdList, startTime, endTime)) {
            return ResultUtil.success(ResultCode.COUPON_EDIT_STATUS_SUCCESS);
        }
        throw new ServiceException(ResultCode.COUPON_EDIT_STATUS_ERROR);
    }

}
