package com.gugugu.haochat.common.interceptors;

import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.dto.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.gugugu.haochat.common.constant.UserConst.ATTRIBUTE_UID_IN_HEADER;

@Order(1)
@Slf4j
@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequestInfo info = new RequestInfo();
        Object uidOptional = request.getAttribute(ATTRIBUTE_UID_IN_HEADER);
        Long uid = Optional.ofNullable(uidOptional).map(Object::toString).map(Long::parseLong).orElse(null);
        String ip = getIp(request);
        info.setUid(uid);
        info.setIp(ip);
        RequestHolderDTO.set(info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolderDTO.remove();
    }

    private String getIp(HttpServletRequest request) {
        return (String) request.getAttribute("X-Real-IP");
    }
}