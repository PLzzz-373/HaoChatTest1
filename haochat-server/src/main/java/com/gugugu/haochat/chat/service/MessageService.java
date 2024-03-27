package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.req.message.RevokeMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;

public interface MessageService {
    ChatMessageResp buildChatMessageResp(Long msgId, boolean b, boolean b1);

    ChatMessageResp.ReplyMsg buildReplyMsg(Long msgId);

    void check(Long uid, ChatMessageReq req);

    void checkReplyMessage(ChatMessageReq req);

    void save(Message message, ChatMessageReq req);

    Boolean revoke(Long uid, RevokeMessageReq req);
}
