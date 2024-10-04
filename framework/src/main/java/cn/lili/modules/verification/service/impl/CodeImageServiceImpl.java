package cn.lili.modules.verification.service.impl;

import cn.hutool.core.codec.Base64;
import cn.lili.cache.Cache;
import cn.lili.cache.CachePrefix;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.modules.verification.service.CodeImageService;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;


import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: nxc
 * @since: 2023/6/7 09:19
 * @description: 图片验证码业务实现层
 */
@Service
public class CodeImageServiceImpl implements CodeImageService {

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private Cache cache;
    @Override
    public Map<String, Object> createCodeImage(String uuid) {
        // 保存验证码信息
        String verifyKey = CachePrefix.CODE_IMAGE.getPrefix() + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码

        String capText = captchaProducerMath.createText();
        capStr = capText.substring(0, capText.lastIndexOf("@"));
        code = capText.substring(capText.lastIndexOf("@") + 1);
        image = captchaProducerMath.createImage(capStr);


        cache.put(verifyKey, code, 600L);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (Exception e)
        {
            throw new ServiceException(ResultCode.ERROR);
        }
        Map<String, Object> resultMap = new HashMap<>();


        resultMap.put("img", Base64.encode(os.toByteArray()));
        return resultMap ;
    }

    @Override
    public boolean check(String uuid,String code) {
        String codeX = (String) cache.get(CachePrefix.CODE_IMAGE.getPrefix() + uuid);
        if (codeX  == null) {
            throw new ServiceException(ResultCode.VERIFICATION_CODE_INVALID);
        }
        else if(codeX.equals(code)){
            //如果有校验标记，则返回校验结果
            cache.remove(CachePrefix.CODE_IMAGE.getPrefix() + uuid);
            return true;
        }
        throw new ServiceException(ResultCode.VERIFICATION_ERROR);
    }
}
