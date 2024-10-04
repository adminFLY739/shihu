package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author wuwenxin
 * @date 2023-11-14 15:43:24
 **/
@Data
@ApiModel(value = "推荐用户列表参数")
public class UserRecommendListForm {

    private Integer currPage;

    private String tenantId;

    private Integer discussId;
}
