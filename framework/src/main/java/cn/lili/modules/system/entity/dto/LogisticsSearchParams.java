package cn.lili.modules.system.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.vo.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Arrays;

/**
 * 商品查询条件
 *
 * @author pikachu
 * @since 2020-02-24 19:27:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsSearchParams extends PageVO {

    private static final long serialVersionUID = 2544015852728566887L;


    @ApiModelProperty(value = "租户id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        queryWrapper.eq("delete_flag", false);
        queryWrapper.orderByDesc("create_time");
        return queryWrapper;
    }

}
