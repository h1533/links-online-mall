package com.hua.mall.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hua.mall.annotation.AuthCheck;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.model.entity.User;
import com.hua.mall.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：管理员校验过滤器
 *
 * @author hua
 * @date 2022/10/15 17:05
 */
@Aspect
@Component
public class UserAuthVerify {

    @Resource
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 当前用户
        User currentUser = userService.loginStatus(request);
        // 拥有指定权限
        if (StringUtils.isNotBlank(mustRole)) {
            String userRole = currentUser.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        return joinPoint.proceed();
    }
}
