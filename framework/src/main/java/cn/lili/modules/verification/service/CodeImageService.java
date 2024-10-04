package cn.lili.modules.verification.service;

import cn.lili.modules.verification.entity.enums.VerificationEnums;

import java.util.Map;

/**
 * @author: nxc
 * @since: 2023/6/7 09:19
 * @description: 图片验证码服务层
 */
public interface CodeImageService {

    /**
    *
    * 生成验证码图片
    *
    *@Param: uuid
    *@return: 图片
    *@Author: nxc
    *@date: 2023/6/7
    */
    Map<String, Object> createCodeImage(String uuid);

    /**
     * 验证码校验
     *
     * @param uuid  用户唯一表示
     * @param code  用户输入的验证码
     * @return 操作结果
     */
    boolean check(String uuid ,String code);
}
