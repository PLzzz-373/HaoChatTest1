package com.gugugu.haochat.chat.domain.vo.req.friend;

import lombok.Data;

@Data
public class AddFriendReq {

    /**
     * 被申请人的ID
     */
    private Long repliedId;

    /**
     * 申请备注
     */
    private String msg;

}
