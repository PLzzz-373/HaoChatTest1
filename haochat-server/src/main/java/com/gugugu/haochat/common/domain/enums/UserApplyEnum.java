package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserApplyEnum {
    /**
     *
     */
    FRIEND(1, "好友"),
    GROUP(2, "群聊"),
    ;
    private final Integer type;
    private final String msg;

    public String getMsg() {
        return msg;
    }

    public Integer getType() {
        return type;
    }
}
