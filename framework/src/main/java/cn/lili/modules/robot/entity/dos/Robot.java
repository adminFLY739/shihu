package cn.lili.modules.robot.entity.dos;

import cn.lili.common.enums.ClientTypeEnum;
import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.mybatis.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Data
@TableName("lf_robot")
@ApiModel(value = "机器人")
@NoArgsConstructor
public class Robot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员用户名")
    private String username;

    @ApiModelProperty(value = "会员密码")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "会员姓名")
    private String studentId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @Min(message = "会员性别参数错误", value = 0)
    @ApiModelProperty(value = "会员性别,1为男，0为女")
    private Integer sex;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "会员生日")
    private Date birthday;

    @ApiModelProperty(value = "会员地址ID")
    private String regionId;

    @ApiModelProperty(value = "会员地址")
    private String region;

    @NotEmpty(message = "手机号码不能为空")
    @ApiModelProperty(value = "手机号码", required = true)
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String mobile;

    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(value = "积分数量")
    private Long point;

    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(value = "积分总数量")
    private Long totalPoint;

    @ApiModelProperty(value = "会员头像")
    private String face;

    @ApiModelProperty(value = "会员状态")
    private Boolean disabled;

    @ApiModelProperty(value = "是否开通店铺")
    private Boolean haveStore;

    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    /**
     * @see ClientTypeEnum
     */
    @ApiModelProperty(value = "客户端")
    private String clientEnum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginDate;

    @ApiModelProperty(value = "会员等级ID")
    private String gradeId;

    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(value = "经验值数量")
    private Long experience;

    @ApiModelProperty(value = "租户id")
    private String tenantIds;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    public static String generateUserId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return timestamp + randomNum;
    }
    public Robot(String nickName) {
        this.username = generateUserId();
        this.password = "111111";
        this.nickName = nickName;
        this.disabled = false;//未审核状态
        this.haveStore = false;
        this.sex = 0;
        this.point = 0L;
        this.totalPoint = 0L;
        this.lastLoginDate = new Date();
        this.face = "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
    }
}