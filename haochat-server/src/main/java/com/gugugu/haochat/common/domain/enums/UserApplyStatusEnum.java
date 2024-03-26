package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserApplyStatusEnum {
    /**
     *
     */
    APPLYING(1, "待审批"),
    AGREED(2, "已同意"),
    REFUSE(3, "已拒绝"),
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
