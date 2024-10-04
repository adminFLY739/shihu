package cn.lili.modules.page.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;

/**
 * @author: nxc
 * @since: 2023/6/14 15:39
 * @description: 意见反馈DTO
 */

@Data
public class FeedbackDTO {

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        queryWrapper.orderByDesc("create_time");


        return queryWrapper;
    }

}
