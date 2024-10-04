package cn.lili.common.aop.annotation;

import java.lang.annotation.*;

/**
 *  该注解可以限制普通用户禁止访问管理员api
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthOnCondition {
}
