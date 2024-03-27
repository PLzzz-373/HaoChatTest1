package com.gugugu.haochat.chat.domain.vo.resp.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp {
    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 用户地区
     */
    private String place;

}
