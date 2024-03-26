package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum WsReqTypeEnum {
    /**
     * 请求登录二维码
     */
    LOGIN(1001, "请求登录二维码"),

    /**
     * 心跳包
     */
    HEARTBEAT(1002, "心跳包"),

    /**
     * 登录认证
     */
    AUTHORIZE(1003, "登录认证"),

    /**
     * 退出登录
     */
    LOGOUT(1004, "退出登录"),
    ;

    private final Integer type;
    private final String desc;

    public static final Map<Integer, WsReqTypeEnum> WS_REQUEST_TYPE_CACHE;

    static {
        WS_REQUEST_TYPE_CACHE = Arrays
                .stream(WsReqTypeEnum.values())
                .collect(
                        Collectors.toMap(
                                WsReqTypeEnum::getType,
                                Function.identity()
                        )
                );
    }

    public static WsReqTypeEnum of(Integer type) {
        return WS_REQUEST_TYPE_CACHE.get(type);
    }
}
