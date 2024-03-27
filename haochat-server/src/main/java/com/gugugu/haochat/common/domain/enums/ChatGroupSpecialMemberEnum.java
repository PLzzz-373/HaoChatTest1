package com.gugugu.haochat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGroupSpecialMemberEnum {

    /**
     * 全体用户
     */
    ALL(0L),
    ;

    private final Long id;

}
