package com.gugugu.haochat.common.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        StringBuffer requestUrl = request.getRequestURL();
        String sessionId = request.getRequestedSessionId();
        Map<String, String[]> parameterMap = request.getParameterMap();

        log.info("ip = {}", ip);
        log.info("requestUrl = {}", requestUrl);
        log.info("sessionId = {}", sessionId);
        log.info("pathInfo = {}", request.getPathInfo());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            log.info("entry = {}", entry);
        }
        return true;
    }
}
