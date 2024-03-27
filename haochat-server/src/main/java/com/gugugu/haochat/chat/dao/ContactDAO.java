package com.gugugu.haochat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.Contact;
import com.gugugu.haochat.chat.domain.vo.req.contact.ChatContactCursorReq;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ContactWithActiveMsg;
import com.gugugu.haochat.chat.mapper.ContactMapper;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContactDAO extends ServiceImpl<ContactMapper, Contact> {

    @Resource
    private ContactMapper contactMapper;
    public void updateReadTime(Long roomId, Date updateTime) {
        lambdaUpdate()
                .eq(Contact::getRoomId,roomId)
                .set(Contact::getUpdateTime,updateTime)
                .update();
    }

    public CursorPageBaseResp<ContactWithActiveMsg, Date> getCursorPage(Long uid, ChatContactCursorReq request) {
        List<ContactWithActiveMsg> list = contactMapper.getCursorPage(uid, request);
        CursorPageBaseResp<ContactWithActiveMsg, Date> contactWithActiveMsgCursorPageBaseResp = new CursorPageBaseResp<>();
        if (list.size() > 0) {
            contactWithActiveMsgCursorPageBaseResp.setCursor(list.get(list.size() - 1).getActiveTime());
        }
        contactWithActiveMsgCursorPageBaseResp.setList(list);
        contactWithActiveMsgCursorPageBaseResp.setIsLast(request.getPageSize() > list.size());
        return contactWithActiveMsgCursorPageBaseResp;
    }

    public Contact getByUidRoomId(Long uid, Long roomId) {
        return this.lambdaQuery().eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    public Contact createContact(Long uid, Long roomId, Date readTime) {
        Contact contact = new Contact();
        contact.setUid(uid);
        contact.setRoomId(roomId);
        contact.setReadTime(readTime);
        this.save(contact);
        return contact;
    }

    public void deleteByRoomId(Long roomId) {
        LambdaQueryWrapper<Contact> wrapper = new QueryWrapper<Contact>()
                .lambda()
                .eq(Contact::getRoomId, roomId);
        this.remove(wrapper);
    }

    public Boolean delContact(Long groupId, Long uid) {
        QueryWrapper<Contact> wrapper = new QueryWrapper<Contact>()
                .eq("room_id", groupId)
                .eq("uid", uid);
        return this.remove(wrapper);
    }

    public void createContactBatch(Long roomId, List<Long> uidList, Date date) {
        List<Contact> contactList = new ArrayList<>();
        uidList.forEach(uid -> {
            Contact contact = new Contact();
            contact.setUid(uid);
            contact.setRoomId(roomId);
            contact.setReadTime(date);
            contactList.add(contact);
        });
        this.saveBatch(contactList);
    }
}
