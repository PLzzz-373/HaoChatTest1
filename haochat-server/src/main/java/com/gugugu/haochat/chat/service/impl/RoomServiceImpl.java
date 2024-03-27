package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.gugugu.haochat.chat.dao.*;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.service.RoomService;
import com.gugugu.haochat.common.domain.enums.ChatGroupSpecialMemberEnum;
import com.gugugu.haochat.common.domain.enums.GroupRoleEnum;
import com.gugugu.haochat.common.domain.enums.RoomTypeEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.vo.resp.GroupMemberBaseInfo;
import com.gugugu.haochat.common.utils.AssertUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Resource
    private RoomDAO roomDAO;
    @Resource
    private RoomFriendDAO roomFriendDAO;
    @Resource
    private GroupMemberDAO groupMemberDAO;
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private RoomGroupDAO roomGroupDAO;
    @Resource
    private ContactDAO contactDAO;
    @Override
    public void check(Long uid, ChatMessageReq req) {
        if(uid.equals(ChatGroupSpecialMemberEnum.ALL.getId())){
            return;
        }
        Long roomId = req.getRoomId();
        Room room = roomDAO.getById(roomId);
        AssertUtil.isNotEmpty(room, ChatErrorEnum.ROOM_NOT_EXIST.getMsg());
        //如果是大群则跳过检验
        if(room.isHotRoom()){
            return;
        }

        AssertUtil.isNotEmpty(room,ChatErrorEnum.ROOM_NOT_EXIST.getMsg());
        this.checkRoomMembership(roomId,uid);
    }

    @Override
    public Boolean checkRoomMembership(Long roomId, Long... uids) {
        //判断用户id
        int length = uids.length;
        if(length == 0){
            return false;
        }
        Room room = roomDAO.getById(roomId);
        //大群跳过检验
        if(room.isHotRoom()){
            return true;
        }
        AssertUtil.isNotEmpty(room,ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        if(room.isRoomFriend()){
            //单聊检查
            Boolean hasUser;
            if(uids.length == 1){
                hasUser = roomFriendDAO.isFriend(roomId, uids[0]);
            }else {
                hasUser = roomFriendDAO.isFriend(Arrays.asList(uids));
            }
            AssertUtil.isTrue(hasUser,ChatErrorEnum.NOT_FRIEND.getMsg());
        }else {
            //群聊检查
            Boolean hasUser = groupMemberDAO.isGroupShip(roomId, CollectionUtil.toList(uids));
            AssertUtil.isTrue(hasUser,ChatErrorEnum.NOT_IN_GROUP.getMsg());
        }
        return true;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGroup(Long uid, String groupName, String groupAvatar) {
        // 1. 创建房间（room）
        Room room = roomDAO.createRoom(RoomTypeEnum.GROUP);
        Long roomId = room.getId();

        // 2. 创建首条消息（message）
        Message message = messageDAO.createFristCreateGroupMessage(uid, roomId, groupName);

        // 2.1 补充房间的最新消息
        roomDAO.updateRoomNewestMsg(roomId, message.getCreateTime(), message.getId());

        // 3. 将群主加入房间，并设置为群主（group_member）
        List<GroupMemberBaseInfo> list = Collections.singletonList(new GroupMemberBaseInfo(uid, GroupRoleEnum.MASTER.getId()));
        groupMemberDAO.createGroup(roomId, groupName, list);

        // 4. 设置群名等信息（room_group）
        roomGroupDAO.createGroup(roomId, groupName, groupAvatar);

        // 5. 给群主创建会话（contact）
        contactDAO.createContact(uid, roomId, message.getCreateTime());

        return roomId;
    }
}
