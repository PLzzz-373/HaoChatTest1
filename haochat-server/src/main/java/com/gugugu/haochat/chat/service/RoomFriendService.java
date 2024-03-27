package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.friend.AddFriendReq;
import com.gugugu.haochat.chat.domain.vo.resp.friend.FriendResp;
import com.gugugu.haochat.user.domain.entity.UserApply;

import java.util.List;

public interface RoomFriendService {
    List<FriendResp> listFriend(Long uid);

    String delFriend(Long uid, Long friendId);

    String applyAddFriend(Long uid, AddFriendReq addFriendReq);

    void agreeAddFriend(UserApply userApply);
}
