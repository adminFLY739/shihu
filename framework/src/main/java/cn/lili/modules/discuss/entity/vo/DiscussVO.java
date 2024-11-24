package cn.lili.modules.discuss.entity.vo;

import cn.lili.common.enums.ClientTypeEnum;
import cn.lili.common.security.sensitive.Sensitive;
import cn.lili.common.security.sensitive.enums.SensitiveStrategy;
import cn.lili.common.utils.BeanUtil;
import cn.lili.modules.BBS.entity.DiscussEntity;
import cn.lili.modules.BBS.entity.PostEntity;
import cn.lili.modules.robot.entity.dos.Robot;
import cn.lili.modules.tenant.entity.dos.Tenant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class DiscussVO implements Serializable {

    private static final long serialVersionUID = 1810890757303309436L;

    @ApiModelProperty(value = "唯一标识", hidden = true)
    private Integer id;

    @ApiModelProperty(value = "uid")
    private String uid;

    @ApiModelProperty(value = "description")
    private String description;

    @ApiModelProperty(value = "title")
    private String title;

    public DiscussVO(DiscussEntity postEntity) {
        Long entityId = postEntity.getId();
        this.setId(entityId.intValue());
        BeanUtil.copyProperties(postEntity, this);
    }


}
