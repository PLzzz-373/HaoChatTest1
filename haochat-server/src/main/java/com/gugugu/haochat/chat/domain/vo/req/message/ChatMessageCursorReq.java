package com.gugugu.haochat.chat.domain.vo.req.message;

import com.gugugu.haochat.common.domain.vo.req.CursorPageBaseReq;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMessageCursorReq extends CursorPageBaseReq<String> {

    /**
     * 房间ID
     */
    @NotNull
    private Long roomId;
}
