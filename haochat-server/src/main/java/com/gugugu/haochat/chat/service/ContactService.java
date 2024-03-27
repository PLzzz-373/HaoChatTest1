package com.gugugu.haochat.chat.service;

import com.gugugu.haochat.chat.domain.vo.req.contact.ChatContactCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatReadMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ChatContactCursorResp;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;

public interface ContactService {
    CursorPageBaseResp<ChatContactCursorResp, Date> listContact(Long uid, ChatContactCursorReq request);

    ChatContactCursorResp getContact(Long uid, Long roomId);

    Boolean readMessage(Long uid, ChatReadMessageReq request);
}
