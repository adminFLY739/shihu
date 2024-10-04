package cn.lili.common.aop.interceptor;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 限制未登录或未实名账号访问
 */
@Component
@Aspect
public class RealNameAuthAspect {

    @Pointcut("@annotation(cn.lili.common.aop.annotation.AuthOnCondition)")
    public void haveAnnotationPer() {
    }

    @Around("haveAnnotationPer()")
    public Object AroundMethodInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        AuthUser currentUser = UserContext.getCurrentUser();
        if (currentUser == null){
            throw new ServiceException(ResultCode.USER_NOT_LOGIN);
        }
        //限制未实名账号访问接口
        if ("0".equals(currentUser.getDisabled())){
            throw new ServiceException(ResultCode.USER_NOT_REALNAME_ERROR);
        }
        Object[] args = joinPoint.getArgs();
        return joinPoint.proceed(args);
    }
}
