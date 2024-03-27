package com.gugugu.haochat.chat.domain.vo.req.message;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class MessageOperationReq {

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long id;

    /**
     * 操作类型 2同意 3拒绝
     */
    @NotNull(message = "操作类型不能为空")
    private Integer status;

}
