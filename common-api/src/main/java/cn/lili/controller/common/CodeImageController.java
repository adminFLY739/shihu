package cn.lili.controller.common;

import cn.lili.cache.limit.annotation.LimitPoint;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.verification.service.CodeImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: nxc
 * @since: 2023/6/7 09:15
 * @description: 获取验证码图片
 */

@Slf4j
@RestController
@RequestMapping("/common/common/getCodeImage")
@Api(tags = "图片证码接口")
public class CodeImageController {

    @Autowired
    CodeImageService codeImageService;

    @LimitPoint(name = "code_image", key = "verification")
    @ApiOperation(value = "获取图片验证码")
    @GetMapping
    public ResultMessage<Object> getCodeImage(@RequestHeader String uuid) {
        return ResultUtil.data(codeImageService.createCodeImage(uuid));

    }
}
