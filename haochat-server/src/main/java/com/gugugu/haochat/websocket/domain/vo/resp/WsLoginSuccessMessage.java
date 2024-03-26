package com.gugugu.haochat.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WsLoginSuccessMessage {
    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 用户头像信息
     */
    private String avatar;

    /**
     * token
     */
    private String token;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户权限 9001普通用户 9002超管
     */
    private Long power;
}
