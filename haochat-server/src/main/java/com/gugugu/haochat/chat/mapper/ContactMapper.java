package com.gugugu.haochat.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gugugu.haochat.chat.domain.entity.Contact;
import com.gugugu.haochat.chat.domain.vo.req.contact.ChatContactCursorReq;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ContactWithActiveMsg;

import java.util.List;

public interface ContactMapper extends BaseMapper<Contact> {
    List<ContactWithActiveMsg> getCursorPage(Long uid, ChatContactCursorReq request);
    ContactWithActiveMsg getContactWithActiveMsg(Long uid, Long contactId);
}
