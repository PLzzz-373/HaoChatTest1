package com.gugugu.haochat.common.interceptors;

import cn.hutool.extra.spring.SpringUtil;
import com.gugugu.haochat.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

import static com.gugugu.haochat.common.constant.UserConst.*;

@Slf4j
@Component
public class PublicTokenInterceptor implements HandlerInterceptor {

    private static final LoginService loginService;

    static {
        loginService = SpringUtil.getBean(LoginService.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取用户登录token
        String token = getToken(request);
        Long uid = loginService.getValidUid(token);
        // 有登录态
        if (!Objects.isNull(uid)) {
            request.setAttribute(ATTRIBUTE_UID_IN_HEADER, uid);
        }
        return true;
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(TOKEN_KEY_IN_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(TOKEN_KEY_IN_HEADER_PREFIX))
                .map(h -> h.substring(TOKEN_KEY_IN_HEADER_PREFIX.length()))
                .orElse("");
    }
}
