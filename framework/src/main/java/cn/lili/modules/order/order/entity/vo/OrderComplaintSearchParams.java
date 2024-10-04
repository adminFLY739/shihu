package cn.lili.modules.order.order.entity.vo;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.modules.order.aftersale.entity.enums.ComplaintStatusEnum;
import cn.lili.modules.order.order.entity.dos.OrderComplaint;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单投诉查询参数
 *
 * @author paulG
 * @since 2020/12/4
 **/
@Data
public class OrderComplaintSearchParams {

    /**
     * @see ComplaintStatusEnum
     */
    @ApiModelProperty(value = "交易投诉状态")
    private String status;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "会员id")
    private String memberId;

    @ApiModelProperty(value = "会员名称")
    private String memberName;

    @ApiModelProperty(value = "商家id")
    private String storeId;

    @ApiModelProperty(value = "商家名称")
    private String storeName;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(status)) {
            queryWrapper.like("complain_status", status);
        }
        if (CharSequenceUtil.isNotEmpty(orderSn)) {
            queryWrapper.like("order_sn", orderSn);
        }
        //按买家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.MEMBER.name())) {
            queryWrapper.eq("me.member_id", UserContext.getCurrentUser().getId());
        }
        //按卖家查询
        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name())) {
            queryWrapper.eq("me.store_id", UserContext.getCurrentUser().getStoreId());
        }

        if (CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.MANAGER.name())
                && CharSequenceUtil.isNotEmpty(storeId)
        ) {
            queryWrapper.eq("me.store_id", storeId);
        }
        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("me.member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(storeName)) {
            queryWrapper.like("store_name", storeName);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }
        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }




}
