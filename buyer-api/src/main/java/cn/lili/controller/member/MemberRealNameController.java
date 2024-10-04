package cn.lili.controller.member;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.entity.dto.MemberRealNameDTO;
import cn.lili.modules.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * 用户审核
 * 用于后台对用户的审核
 *
 * @author wwx
 * @since 2023/10/03 15:19
 */
@RestController
@Api(tags = "买家端,用户审核接口")
@RequestMapping("/buyer/member/realName")
public class MemberRealNameController {

    @Resource
    private MemberService memberService;

    @ApiOperation(value = "会员提交审核")
    @PutMapping("/applying")
    private ResultMessage<Boolean> realNameAuthentication(@Valid MemberRealNameDTO memberRealNameDTO){
        return ResultUtil.data(memberService.userRealNameApplying(memberRealNameDTO));
    }
}
