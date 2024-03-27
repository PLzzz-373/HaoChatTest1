package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberResp;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMemberCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.ResultUtil;

public interface ChatService {
    ResultUtil<ChatMessageResp> send(Long uid, ChatMessageReq req);

    Boolean isTemUser(Long uid);

    CursorPageBaseResp<ChatMemberResp, String> listMember(Long uid, ChatMemberCursorReq req);

    CursorPageBaseResp<ChatMessageResp, String> listMessage(Long uid, ChatMessageCursorReq req);

}
