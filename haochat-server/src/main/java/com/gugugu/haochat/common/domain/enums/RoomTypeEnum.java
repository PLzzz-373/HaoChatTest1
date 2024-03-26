package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RoomTypeEnum {
    /**
     *
     */
    GROUP(1, "群聊"),
    FRIEND(2, "单聊"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, RoomTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(RoomTypeEnum.values()).collect(Collectors.toMap(RoomTypeEnum::getType, Function.identity()));
    }

    public static RoomTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
