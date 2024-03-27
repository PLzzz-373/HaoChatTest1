package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gugugu.haochat.chat.dao.ContactDAO;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.domain.entity.Contact;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.req.contact.ChatContactCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatReadMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ChatContactCursorResp;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ContactWithActiveMsg;
import com.gugugu.haochat.chat.mapper.ContactMapper;
import com.gugugu.haochat.common.cache.RoomFriendCache;
import com.gugugu.haochat.common.domain.vo.resp.FriendBaseInfo;
import com.gugugu.haochat.common.domain.vo.resp.RoomBaseInfo;
import com.gugugu.haochat.chat.service.ContactService;
import com.gugugu.haochat.chat.service.adapter.ContactAdapter;
import com.gugugu.haochat.chat.service.strategy.msg.factory.MessageHandlerFactory;
import com.gugugu.haochat.common.cache.RoomCache;
import com.gugugu.haochat.common.cache.RoomGroupCache;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.constant.UserConst;
import com.gugugu.haochat.common.domain.dto.GroupBaseInfo;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.domain.enums.RoomTypeEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {
    @Resource
    private ContactDAO contactDAO;
    @Resource
    private RoomCache roomCache;
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private UserCache userCache;
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    private RoomFriendCache roomFriendCache;
    @Resource
    private ContactMapper contactMapper;
    @Override
    public CursorPageBaseResp<ChatContactCursorResp, Date> listContact(Long uid, ChatContactCursorReq request) {
        CursorPageBaseResp<ContactWithActiveMsg, Date> cursorPage = contactDAO.getCursorPage(uid, request);

        List<ChatContactCursorResp> list = cursorPage.getList().stream().map(contact -> this.buildChatContactResp(uid, contact)).collect(Collectors.toList());

        return ContactAdapter.buildContactCursorPage(list, cursorPage);
    }

    @Override
    public ChatContactCursorResp getContact(Long uid, Long roomId) {
        // 1. 判断房间是否存在
        RoomBaseInfo roomBaseInfo = roomCache.getRoomBaseInfoById(roomId);
        AssertUtil.isNotEmpty(roomBaseInfo, ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        // 2. 获取会话信息
        Contact contact = contactDAO.getByUidRoomId(uid, roomId);
        if (ObjectUtil.isNull(contact)) {
            // 2.1 创建会话
            contact = contactDAO.createContact(uid, roomId, new Date());
        }

        // 3. 构造响应体
        ContactWithActiveMsg contactWithActiveMsg = contactMapper.getContactWithActiveMsg(uid, contact.getId());
        return this.buildChatContactResp(uid, contactWithActiveMsg);
    }

    @Override
    public Boolean readMessage(Long uid, ChatReadMessageReq request) {
        Long roomId = request.getRoomId();
        // 临时用户不进行上报
        if (UserConst.TEMP_USER_UID.equals(uid)) {
            return true;
        } else {
            Contact contact = contactDAO.getByUidRoomId(uid, roomId);
            if (!ObjectUtil.isNull(contact)) {
                Contact update = new Contact();
                update.setId(contact.getId());
                update.setReadTime(new Date());
                contactDAO.updateById(update);
            } else {
                Contact insert = new Contact();
                insert.setUid(uid);
                insert.setRoomId(roomId);
                insert.setReadTime(new Date());
                contactDAO.save(insert);
            }
        }
        return true;
    }

    private ChatContactCursorResp buildChatContactResp(Long uid, ContactWithActiveMsg contact) {
        ChatContactCursorResp chatContactCursorResp = new ChatContactCursorResp();
        Long roomId = contact.getRoomId();

        chatContactCursorResp.setRoomId(roomId);
        if (!UserConst.TEMP_USER_UID.equals(uid)) {
            chatContactCursorResp.setActiveTime(Optional.ofNullable(contact.getActiveTime()).orElse(new Date()));
        }

        // 通过房间ID获取房间信息
        RoomBaseInfo roomBaseInfo = roomCache.getRoomBaseInfoById(roomId);
        chatContactCursorResp.setType(roomBaseInfo.getType());
        chatContactCursorResp.setHotFlag(roomBaseInfo.getHotFlag());

        Long lastMsgId = contact.getLastMsgId();
        if (lastMsgId != null) {
            Message message = messageDAO.getById(lastMsgId);
            AssertUtil.isNotEmpty(message, ChatErrorEnum.MESSAGE_NOT_EXIST.getMsg());

            Long fromUid = message.getFromUid();
            UserBaseInfo baseInfo = userCache.getBaseUserInfoByUid(fromUid);
            String showInContactMessage = MessageHandlerFactory.getStrategyNoNull(message.getType()).showInContactMessage(message);
            chatContactCursorResp.setText(baseInfo.getName() + ": " + showInContactMessage);
        } else {
            // 1. 判断有没有最新消息
            chatContactCursorResp.setText("暂无消息");
        }

        if (RoomTypeEnum.GROUP.getType().equals(roomBaseInfo.getType())) {
            // 群聊
            GroupBaseInfo groupBaseInfo = roomGroupCache.getBaseInfoById(roomId);
            AssertUtil.isNotEmpty(groupBaseInfo, CommonErrorEnum.SYSTEM_ERROR.getMsg());

            // 设房间名
            String name = groupBaseInfo.getName();
            if (StrUtil.isEmpty(name)) {
                chatContactCursorResp.setName("没有名字的房间");
            } else {
                chatContactCursorResp.setName(name);
            }

            // 房间头像
            chatContactCursorResp.setAvatar(groupBaseInfo.getAvatar());

        } else {
            // 单聊
            FriendBaseInfo roomFriendBaseInfo = roomFriendCache.getBaseInfoById(roomId);
            AssertUtil.isNotEmpty(roomFriendBaseInfo, CommonErrorEnum.SYSTEM_ERROR.getMsg());

            Long uid1 = roomFriendBaseInfo.getUid1();
            Long uid2 = roomFriendBaseInfo.getUid2();
            if (uid.equals(uid1)) {
                UserBaseInfo userBaseInfo = userCache.getBaseUserInfoByUid(uid2);
                chatContactCursorResp.setName(userBaseInfo.getName());
                chatContactCursorResp.setAvatar(userBaseInfo.getAvatar());
            } else if (uid.equals(uid2)) {
                UserBaseInfo userBaseInfo = userCache.getBaseUserInfoByUid(uid1);
                chatContactCursorResp.setName(userBaseInfo.getName());
                chatContactCursorResp.setAvatar(userBaseInfo.getAvatar());
            }
        }

        Integer count = messageDAO.getUnReadCountByReadTime(roomId, contact.getReadTime());
        chatContactCursorResp.setUnreadCount(count);

        return chatContactCursorResp;
    }
}
