package cn.lili.modules.wallet.serviceimpl;


import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.wallet.entity.dos.MemberWithdrawApply;
import cn.lili.modules.wallet.entity.dto.MemberWalletUpdateDTO;
import cn.lili.modules.wallet.entity.dto.MemberWithdrawalMessage;
import cn.lili.modules.wallet.entity.enums.DepositServiceTypeEnum;
import cn.lili.modules.wallet.entity.enums.MemberWithdrawalDestinationEnum;
import cn.lili.modules.wallet.entity.enums.WithdrawStatusEnum;
import cn.lili.modules.wallet.entity.vo.MemberWalletVO;
import cn.lili.modules.wallet.entity.vo.MemberWithdrawApplyQueryVO;
import cn.lili.modules.wallet.mapper.MemberWithdrawApplyMapper;
import cn.lili.modules.wallet.service.MemberWalletService;
import cn.lili.modules.wallet.service.MemberWithdrawApplyService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.rocketmq.tags.MemberTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 会员提现申请业务层实现
 *
 * @author pikachu
 * @since 2020-02-25 14:10:16
 */
@Service
public class MemberWithdrawApplyServiceImpl extends ServiceImpl<MemberWithdrawApplyMapper, MemberWithdrawApply> implements MemberWithdrawApplyService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Resource
    private MemberWithdrawApplyMapper memberWithdrawApplyMapper;

    /**
     * 会员余额
     */
    @Autowired
    private MemberWalletService memberWalletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean audit(String applyId, Boolean result, String remark) {
        MemberWithdrawalMessage memberWithdrawalMessage = new MemberWithdrawalMessage();
        //查询申请记录
        MemberWithdrawApply memberWithdrawApply = this.getById(applyId);
        memberWithdrawApply.setInspectRemark(remark);
        memberWithdrawApply.setInspectTime(new Date());
        if (memberWithdrawApply != null) {
            //获取账户余额
            MemberWalletVO memberWalletVO = memberWalletService.getMemberWallet(memberWithdrawApply.getMemberId(),memberWithdrawApply.getTenantId());
            //校验金额是否满足提现，因为是从冻结金额扣减，所以校验的是冻结金额
            if (memberWalletVO.getMemberFrozenWallet() < memberWithdrawApply.getApplyMoney()) {
                throw new ServiceException(ResultCode.WALLET_WITHDRAWAL_FROZEN_AMOUNT_INSUFFICIENT);
            }
            //如果审核通过 则微信直接提现，反之则记录审核状态
            if (Boolean.TRUE.equals(result)) {
                memberWithdrawApply.setApplyStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                //提现，微信提现成功后扣减冻结金额
                Boolean bool = memberWalletService.withdrawal(memberWithdrawApply);
                if (Boolean.TRUE.equals(bool)) {
                    memberWithdrawalMessage.setStatus(WithdrawStatusEnum.VIA_AUDITING.name());
                    //保存修改审核记录
                    this.updateById(memberWithdrawApply);
                    //记录日志
                    memberWalletService.reduceFrozen(
                            new MemberWalletUpdateDTO(memberWithdrawApply.getApplyMoney(), memberWithdrawApply.getMemberId(), "审核通过，余额提现",memberWithdrawApply.getTenantId(),DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()))
                    ;
                } else {
                    //如果提现失败则无法审核
                    throw new ServiceException(ResultCode.WALLET_APPLY_ERROR);
                }
            } else {
                memberWithdrawalMessage.setStatus(WithdrawStatusEnum.FAIL_AUDITING.name());
                //如果审核拒绝 审核备注必填
                if (StringUtils.isEmpty(remark)) {
                    throw new ServiceException(ResultCode.WALLET_REMARK_ERROR);
                }
                memberWithdrawApply.setApplyStatus(WithdrawStatusEnum.FAIL_AUDITING.name());
                //保存修改审核记录
                this.updateById(memberWithdrawApply);
                //需要从冻结金额扣减到余额
                memberWalletService.increaseWithdrawal(new MemberWalletUpdateDTO(memberWithdrawApply.getApplyMoney(), memberWithdrawApply.getMemberId(), "审核拒绝，提现金额解冻到余额", memberWithdrawApply.getTenantId(),DepositServiceTypeEnum.WALLET_WITHDRAWAL.name()));
            }
            //发送审核消息
            memberWithdrawalMessage.setMemberId(memberWithdrawApply.getMemberId());
            memberWithdrawalMessage.setPrice(memberWithdrawApply.getApplyMoney());
            memberWithdrawalMessage.setDestination(MemberWithdrawalDestinationEnum.WECHAT.name());

            String destination = rocketmqCustomProperties.getMemberTopic() + ":" + MemberTagsEnum.MEMBER_WITHDRAWAL.name();
            rocketMQTemplate.asyncSend(destination, memberWithdrawalMessage, RocketmqSendCallbackBuilder.commonCallback());
            return true;
        }
        throw new ServiceException(ResultCode.WALLET_APPLY_ERROR);
    }


    @Override
    public IPage<MemberWithdrawApply> getMemberWithdrawPage(PageVO pageVO, MemberWithdrawApplyQueryVO memberWithdrawApplyQueryVO) {

        return memberWithdrawApplyMapper.getMemberWithdrawApplyList(PageUtil.initPage(pageVO),memberWithdrawApplyQueryVO.queryWrapper());
    }
}
