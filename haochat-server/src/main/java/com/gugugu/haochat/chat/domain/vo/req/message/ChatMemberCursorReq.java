package com.gugugu.haochat.chat.domain.vo.req.message;

import com.gugugu.haochat.common.domain.vo.req.CursorPageBaseReq;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMemberCursorReq extends CursorPageBaseReq<String> {

    /**
     * 房间号
     */
    @NotNull
    private Long roomId = 1L;

    /**
     * 在线状态
     */
    @NotNull
    private Integer activeStatus;
}
