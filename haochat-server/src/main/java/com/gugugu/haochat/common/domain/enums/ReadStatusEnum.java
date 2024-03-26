package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReadStatusEnum {
    /**
     *
     */
    UNREAD(1, "未读"),
    READ(2, "已读"),
    ;
    private final Integer status;
    private final String msg;

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
