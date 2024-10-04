package cn.lili.modules.promotion.entity.dto.search;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 砍价活动参与实体类
 *
 * @author qiuqiu
 * @date 2020-7-1 10:44 上午
 */
@Data
@ApiModel(value = "砍价活动参与记录查询对象")
public class KanjiaActivityQuery {


    private static final long serialVersionUID = -1583030890805926292L;

    @ApiModelProperty(value = "货品名称")
    private String goodsName;

    @ApiModelProperty(value = "会员id", hidden = true)
    private String memberId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private String tenantId;

    public <T> QueryWrapper<T> wrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(goodsName)) {
            queryWrapper.like("me.goods_name", goodsName);
        }
        if (memberId != null) {
            queryWrapper.eq("member_id", memberId);
        }
        if (tenantId != null) {
          queryWrapper.eq("tenant_id", tenantId);
        }
        queryWrapper.eq("me.delete_flag", false);
        queryWrapper.orderByDesc("me.create_time");
        return queryWrapper;
    }
}
