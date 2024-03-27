package com.gugugu.haochat.chat.domain.vo.req.message;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RevokeMessageReq {

    /**
     * 消息ID
     */
    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long msgId;

}
