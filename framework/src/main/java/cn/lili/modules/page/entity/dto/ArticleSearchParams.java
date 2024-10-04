package cn.lili.modules.page.entity.dto;

import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.vo.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品查询条件
 *
 * @author pikachu
 * @since 2020-02-24 19:27:20
 */
@Data
public class ArticleSearchParams extends PageVO {

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "分类类型")
    private String type;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(categoryId), "category_id", categoryId);
        queryWrapper.eq(CharSequenceUtil.isNotEmpty(tenantId),"tenant_id",tenantId);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        return queryWrapper;
    }
}
