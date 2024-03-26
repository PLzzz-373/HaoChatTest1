package com.gugugu.haochat.common.domain.enums.user;

import lombok.Getter;

@Getter
public enum RoleEnum {
    /**
     * 超级管理员
     */
    SUPER_ADMIN(9001),

    /**
     * blackchat管理员
     */
    GROUP_ADMIN(9002),

    /**
     * 普通用户
     */
    USER(9003);


    public final long value;

    RoleEnum(long value) {
        this.value = value;
    }
}
