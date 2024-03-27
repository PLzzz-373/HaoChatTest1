package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;

public interface RoomService {
    void check(Long uid, ChatMessageReq req);

    Boolean checkRoomMembership(Long roomId, Long... uids);

    Long createGroup(Long uid, String groupName, String groupAvatar);

}
