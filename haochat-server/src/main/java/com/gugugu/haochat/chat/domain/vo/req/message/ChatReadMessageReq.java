package com.gugugu.haochat.chat.domain.vo.req.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatReadMessageReq {

    /**
     * 房间ID
     */
    @NotNull
    private Long roomId;
}
