package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.ContactDAO;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.dao.RoomDAO;
import com.gugugu.haochat.chat.dao.RoomFriendDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.domain.vo.req.friend.AddFriendReq;
import com.gugugu.haochat.chat.domain.vo.resp.friend.FriendResp;
import com.gugugu.haochat.chat.service.RoomFriendService;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.enums.RoomTypeEnum;
import com.gugugu.haochat.common.domain.enums.UserMessageEnum;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.UserErrorEnum;
import com.gugugu.haochat.common.event.MessageSendEvent;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.user.dao.UserApplyDAO;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.IpDetail;
import com.gugugu.haochat.user.domain.IpInfo;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.entity.UserApply;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class RoomFriendServiceImpl implements RoomFriendService {
    @Resource
    private RoomFriendDAO roomFriendDAO;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserCache userCache;
    @Resource
    private ContactDAO contactDAO;
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private UserApplyDAO userApplyDAO;
    @Resource
    private RoomDAO roomDAO;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public List<FriendResp> listFriend(Long uid) {
        List<Long> friendUidList = roomFriendDAO.listFriendIdByUid(uid);
        List<User> userList = userDAO.getBath(friendUidList);

        return userList.stream().map(user -> {
            FriendResp friendResp = new FriendResp();
            friendResp.setUid(user.getId());

            Long roomId = roomFriendDAO.getRoomIdByUid(uid, user.getId());
            friendResp.setRoomId(roomId);

            IpInfo ipInfo = Optional.ofNullable(user.getIpInfo()).orElse(new IpInfo());
            if (ObjectUtil.isNull(ipInfo)) {
                friendResp.setPlace("未知");
            } else {
                IpDetail ipDetail = Optional.ofNullable(ipInfo.getUpdateIpDetail()).orElse(new IpDetail());
                if (ObjectUtil.isNull(ipDetail)) {
                    friendResp.setPlace("未知");
                } else {
                    friendResp.setPlace(ipDetail.getCity());
                }
            }
            return friendResp;
        }).collect(Collectors.toList());
    }

    @Override
    public String delFriend(Long uid, Long friendId) {
        // 1. 判断用户是否存在
        Boolean isExist = userCache.isExistUsers(Collections.singletonList(friendId));
        AssertUtil.isTrue(isExist, UserErrorEnum.USER_NOT_EXIST.getMsg());

        // 2. 删除房间
        Long roomId = roomFriendDAO.delFriend(uid, friendId);
        AssertUtil.isNotEmpty(roomId, CommonErrorEnum.SYSTEM_ERROR.getMsg());

        // 3. 删除会话
        contactDAO.deleteByRoomId(roomId);

        // 4. 删除聊天记录
        messageDAO.deleteByRoomId(roomId);

        return UserMessageEnum.DEL_FRIEND_SUCCESS.getMsg();
    }

    @Override
    public String applyAddFriend(Long uid, AddFriendReq addFriendReq) {
        Long repliedId = addFriendReq.getRepliedId();
        String msg = addFriendReq.getMsg();

        // 1. 判断用户是否存在
        Boolean isExistUsers = userCache.isExistUsers(Collections.singletonList(repliedId));
        AssertUtil.isTrue(isExistUsers, UserErrorEnum.USER_NOT_EXIST.getMsg());

        // 2. 判断是否已经是好友关系
        Boolean isFriend = roomFriendDAO.isFriend(Arrays.asList(uid, repliedId));
        AssertUtil.isFalse(isFriend, UserErrorEnum.ALREADY_FRIEND.getMsg());

        // 2. 判断是否已经是提交过了好友申请
        Boolean isApplying = userApplyDAO.isApplyingFriend(uid, repliedId);
        AssertUtil.isFalse(isApplying, UserErrorEnum.REPEAT_APPLY.getMsg());

        // 3. 添加好友
        Boolean isAddFriend = userApplyDAO.addFriend(uid, repliedId, msg);
        AssertUtil.isTrue(isAddFriend, UserErrorEnum.COMMIT_APPLY_FAIL.getMsg());

        return UserMessageEnum.COMMIT_APPLY_SUCESS.getMsg();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreeAddFriend(UserApply userApply) {
        // 1. 创建房间（room）
        Room room = roomDAO.createRoom(RoomTypeEnum.FRIEND);
        Long roomId = room.getId();

        // 2. 生成好友欢迎消息（message）
        Long uid = userApply.getUid();
        Long targetId = userApply.getTargetId();
        Message message = messageDAO.createFristAddFriendMessage(roomId, uid, targetId);

        // 2.1 补充房间的最新消息
        roomDAO.updateRoomNewestMsg(roomId, message.getCreateTime(), message.getId());

        // 3. 创建会话并更新最新消息（contact）
        contactDAO.createContact(uid, roomId, message.getCreateTime());
        contactDAO.createContact(targetId, roomId, message.getCreateTime());

        // 4. 创建好友关系
        roomFriendDAO.addFriend(roomId, uid, targetId);

        // 5. 发布消息事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, message));
    }
}
