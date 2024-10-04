
package cn.lili.modules.BBS.validator;


import cn.lili.common.exception.LinfengException;
import cn.lili.common.utils.StringUtils;

/**
 * 数据校验
 *
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new LinfengException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new LinfengException(message);
        }
    }
}
