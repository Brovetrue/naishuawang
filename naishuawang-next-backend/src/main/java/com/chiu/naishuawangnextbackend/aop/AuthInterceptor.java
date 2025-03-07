package com.chiu.naishuawangnextbackend.aop;

import com.chiu.naishuawangnextbackend.annotation.AuthCheck;
import com.chiu.naishuawangnextbackend.exception.BusinessException;
import com.chiu.naishuawangnextbackend.exception.ErrorCode;
import com.chiu.naishuawangnextbackend.model.entity.User;
import com.chiu.naishuawangnextbackend.model.enums.UserRoleEnum;
import com.chiu.naishuawangnextbackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@Aspect
//@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 拦截带有AuthCheck注解的方法
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole =  authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        //获取当前登录用户
        User LoginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限就放着
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }
        // 必须有权限才会通过以下的代码
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(LoginUser.getUserRole());
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //要求必须有管理员权限，但用户没有管理员权限，就拒绝
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //通过权限，放行
        return joinPoint.proceed();

    }


}
