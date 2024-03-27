package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum FriendStatusEnum {
    /**
     *
     */
    NORMAL(0, "正常"),
    FORBIDDEN(1, "禁用"),
    ;

    private final Integer code;
    private final String desc;

    public static final Map<Integer, FriendStatusEnum> CACHE;

    static {
        CACHE = Arrays.stream(FriendStatusEnum.values()).collect(Collectors.toMap(FriendStatusEnum::getCode, Function.identity()));
    }

    public static FriendStatusEnum of(Integer code) {
        return CACHE.get(code);
    }
}
