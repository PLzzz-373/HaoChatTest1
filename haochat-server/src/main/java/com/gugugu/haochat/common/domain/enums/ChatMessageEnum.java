package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatMessageEnum {
    /**
     *
     */
    EXIT_GROUP_SUCCESS(1001, "退出群聊成功"),
    DEL_GROUP_SUCCESS(1002, "删除群聊成功"),
    ;
    private final Integer code;
    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
