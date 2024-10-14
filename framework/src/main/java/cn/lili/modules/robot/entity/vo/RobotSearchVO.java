package cn.lili.modules.robot.entity.vo;

import cn.lili.common.enums.SwitchEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员搜索VO
 *
 * @author Bulbasaur
 * @since 2020/12/15 10:48
 */
@Data
public class RobotSearchVO {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "用户手机号码")
    private String mobile;

    /**
     * @see SwitchEnum
     */
    @ApiModelProperty(value = "会员状态")
    private String disabled;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "用户审核列表请求的标志")
    private Boolean applying;
}
