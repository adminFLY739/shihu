package cn.lili.modules.BBS.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "帖子点赞")
public class ManagerAddCollectionForm {
    @ApiModelProperty(value = "帖子id")
    private Integer id;

    @ApiModelProperty(value = "帖子的用户id")
    private String uid;

    @ApiModelProperty(value = "用户id")
    private String thumbUid;
}
