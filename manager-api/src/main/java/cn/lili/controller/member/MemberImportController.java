package cn.lili.controller.member;

import cn.lili.common.context.ThreadContextHolder;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.service.MemberImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;

/**
 * @author: nxc
 * @since: 2023/7/5 16:49
 * @description: 用户导入
 */

@Api(tags = "用户导入")
@RestController
@RequestMapping("/manager/member/import")
public class MemberImportController {

    @Autowired
    private MemberImportService memberImportService;


    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传文件，用户批量添加")
    public ResultMessage<Object> importExcel(@RequestPart("files") MultipartFile files) {
        try {
            memberImportService.importExcel(files);
            return ResultUtil.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.ERROR);
        }

    }


    @ApiOperation(value = "下载导入模板", produces = "application/octet-stream")
    @GetMapping(value = "/downLoad")
    public void download() {
        HttpServletResponse response = ThreadContextHolder.getHttpResponse();

        memberImportService.download(response);
    }


}
