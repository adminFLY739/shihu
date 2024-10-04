package cn.lili.modules.member.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.vo.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评价查询条件
 *
 * @author Bulbasaur
 * @since 2020/11/30 14:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EvaluationQueryParams extends PageVO {

    private static final long serialVersionUID = -3753780054181776735L;
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "买家ID")
    private String memberId;

    @ApiModelProperty(value = "skuID")
    private String skuId;

    @ApiModelProperty(value = "会员名称")
    private String memberName;

    @ApiModelProperty(value = "卖家名称")
    private String storeName;

    @ApiModelProperty(value = "卖家ID")
    private String storeId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品ID")
    private String goodsId;

    @ApiModelProperty(value = "好中差评 , GOOD：好评，MODERATE：中评，WORSE：差评", allowableValues = "GOOD,MODERATE,WORSE")
    private String grade;

    @ApiModelProperty(value = "是否有图")
    private String haveImage;

    @ApiModelProperty(value = "评论日期--开始时间")
    private String startTime;

    @ApiModelProperty(value = "评论日期--结束时间")
    private String endTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "租户id")
    private String tenantId;



    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(id)) {
            queryWrapper.eq("me.id", id);
        }
        if (CharSequenceUtil.isNotEmpty(startTime) && CharSequenceUtil.isNotEmpty(endTime)) {
            queryWrapper.between("me.create_time", startTime, endTime);
        }
        if (CharSequenceUtil.isNotEmpty(grade)) {
            queryWrapper.eq("me.grade", grade);
        }
        if (CharSequenceUtil.isNotEmpty(goodsName)) {
            queryWrapper.like("goods_name", goodsName);
        }
        if (CharSequenceUtil.isNotEmpty(storeName)) {
            queryWrapper.like("me.store_name", storeName);
        }
        if (CharSequenceUtil.isNotEmpty(memberName)) {
            queryWrapper.like("me.member_name", memberName);
        }
        if (CharSequenceUtil.isNotEmpty(goodsId)) {
            queryWrapper.eq("goods_id", goodsId);
        }
        if (CharSequenceUtil.isNotEmpty(skuId)) {
            queryWrapper.eq("me.sku_id", skuId);
        }
        if (CharSequenceUtil.isNotEmpty(storeId)) {
            queryWrapper.eq("me.store_id", storeId);
        }
        if (CharSequenceUtil.isNotEmpty(memberId)) {
            queryWrapper.eq("me.member_id", memberId);
        }
        if (CharSequenceUtil.isNotEmpty(haveImage)) {
            queryWrapper.eq("have_image", haveImage);
        }
        if (CharSequenceUtil.isNotEmpty(status)) {
            queryWrapper.eq("me.status", status);
        }
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.like("tenant_id", tenantId);
        }
        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }
}
