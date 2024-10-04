package cn.lili.modules.member.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: nxc
 * @since: 2023/7/5 16:51
 * @description: 用户导入业务层接口
 */
public interface MemberImportService {

    /**
     * 下载导入列表
     * @param response
     */
    void download(HttpServletResponse response);

    /**
     * 导入商品
     */
    void importExcel(MultipartFile files) throws Exception;
}
