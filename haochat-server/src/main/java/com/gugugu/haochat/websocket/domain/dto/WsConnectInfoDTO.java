package com.gugugu.haochat.websocket.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsConnectInfoDTO {

    /**
     * 用户ID（如果用户登录了，就进行存储）
     */
    private Long uid;

}