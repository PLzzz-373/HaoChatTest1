package com.gugugu.haochat.common.domain.dto;

import lombok.Data;

@Data
public class RequestInfo {
    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 用户IP
     */
    private String ip;
}
