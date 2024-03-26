package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserMessageEnum {
    /**
     *
     */
    DEL_FRIEND_SUCCESS(1001, "删除好友成功"),
    COMMIT_APPLY_SUCESS(1002, "提交申请成功"),
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
