package com.gugugu.haochat.chat.service.impl;

import com.gugugu.haochat.chat.dao.GroupMemberDAO;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.dao.RoomDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberExtraResp;
import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberResp;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMemberCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.service.ChatService;
import com.gugugu.haochat.chat.service.MessageService;
import com.gugugu.haochat.chat.service.RoomService;
import com.gugugu.haochat.chat.service.adapter.GroupMemberAdapter;
import com.gugugu.haochat.chat.service.adapter.MessageAdapter;
import com.gugugu.haochat.chat.service.strategy.msg.AbstractMessageHandler;
import com.gugugu.haochat.chat.service.strategy.msg.factory.MessageHandlerFactory;
import com.gugugu.haochat.common.cache.RoomGroupCache;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.domain.enums.ChatActiveStatusEnum;
import com.gugugu.haochat.common.domain.enums.GroupRoleEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.event.MessageSendEvent;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.common.utils.ResultUtil;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.IpDetail;
import com.gugugu.haochat.user.domain.IpInfo;
import com.gugugu.haochat.user.domain.entity.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gugugu.haochat.common.constant.UserConst.TEMP_USER_UID;
@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private RoomService roomService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private RoomDAO roomDAO;
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserCache userCache;
    @Resource
    private GroupMemberDAO groupMemberDAO;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil<ChatMessageResp> send(Long uid, ChatMessageReq req) {
        //检查是否是临时用户
        Boolean isTemp = this.isTemUser(uid);
        AssertUtil.isFalse(isTemp, ChatErrorEnum.TEMP_USER_NOT_ALLOWED.getMsg());
        //检查用户与房间的关系
        roomService.check(uid,req);
        //检查用户和发送的消息
        messageService.check(uid, req);

        //保存消息
        Message message = messageDAO.saveByUidAndChatMessageReq(uid, req);

        //保存与回复消息间隔数
        Long roomId = req.getRoomId();
        Long messageId = message.getId();
        Long replyMessageId = req.getReplyMessageId();
        messageDAO.saveGapCount(roomId,replyMessageId,messageId);

        //让各自消息处理器保存消息
        messageService.save(message,req);

        //构建消息响应体
        ChatMessageResp chatMessageResp = messageService.buildChatMessageResp(messageId, true, true);
        //发送消息事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this,message));
        return ResultUtil.success(chatMessageResp);
    }

    @Override
    public Boolean isTemUser(Long uid) {
        return TEMP_USER_UID.equals(uid);
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp, String> listMember(Long uid, ChatMemberCursorReq req) {
        Long roomId = req.getRoomId();
        //检查房间是否存在
        Room room = roomDAO.getById(roomId);
        AssertUtil.isNotEmpty(room,ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        //判断热点群聊
        List<Long> uidList;
        if(room.isHotRoom()){
            uidList = null;
        }else {
            uidList = roomGroupCache.getGroupUidByRoomId(roomId);
            Boolean isMember = roomService.checkRoomMembership(roomId, uid);
            AssertUtil.isTrue(isMember,ChatErrorEnum.NOT_IN_GROUP.getMsg());
        }

        //获取数据
        CursorPageBaseResp<User, String> userCursorPageBaseResp;
        Integer activeStatus = req.getActiveStatus();
        Integer pageSize = req.getPageSize();
        ChatMemberExtraResp chatMemberExtraResp = new ChatMemberExtraResp();
        if(ChatActiveStatusEnum.ONLINE.getStatus().equals(activeStatus)){
            //获取在线成员
            userCursorPageBaseResp =  userDAO.getCursorPage(uidList, req);
            //判断当前获取到的数量是否小于pagesize
            int size = userCursorPageBaseResp.getList().size();
            if(size < pageSize){
                //从离线列表填补
                int remainCount = req.getPageSize() - size;
                req.setPageSize(remainCount);
                req.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
                CursorPageBaseResp<User,String> cursorPage = userDAO.getCursorPage(uidList,req);
                userCursorPageBaseResp.getList().addAll(cursorPage.getList());
                userCursorPageBaseResp.setCursor(cursorPage.getCursor());
                chatMemberExtraResp.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());

            }else {
                chatMemberExtraResp.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());

            }
        }else {
            //获取离线成员
            userCursorPageBaseResp = userDAO.getCursorPage(uidList,req);
            req.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());

        }

        if(uidList != null){
            chatMemberExtraResp.setTotalCount(uidList.size());
        }else {
            Long totalCount = userCache.getTotalCount();
            chatMemberExtraResp.setTotalCount(Math.toIntExact(totalCount));
        }
        userCursorPageBaseResp.setExtraInfo(chatMemberExtraResp);

        //组装数据
        List<ChatMemberResp> list = userCursorPageBaseResp.getList().stream().map(User::getId).map(id -> {
            UserBaseInfo baseUserInfo = userCache.getBaseUserInfoByUid(id);
            ChatMemberResp chatMemberResp = ChatMemberResp.builder()
                    .uid(id)
                    .name(baseUserInfo.getName())
                    .avatar(baseUserInfo.getAvatar())
                    .activeStatus(baseUserInfo.getActiveStatus())
                    .build();
            if (!room.isHotRoom()) {
                Integer roleId = groupMemberDAO.getRoleId(req.getRoomId(), id);
                if (!GroupRoleEnum.MEMBER.getId().equals(roleId)) {
                    chatMemberResp.setRoleId(roleId);
                }
            }
            return chatMemberResp;
        }).collect(Collectors.toList());
        return GroupMemberAdapter.buildChatMemberCursorPage(list,userCursorPageBaseResp);
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp, String> listMessage(Long uid, ChatMessageCursorReq req) {
        Long roomId = req.getRoomId();
        //检查房间号是否存在
        Room room = roomDAO.getById(roomId);
        AssertUtil.isNotEmpty(room, ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        //判断是否热点群聊
        if(!room.isHotRoom()){
            //检查用户是否在房间内
            Boolean isMember = roomService.checkRoomMembership(roomId, uid);
            AssertUtil.isTrue(isMember,ChatErrorEnum.NOT_IN_GROUP.getMsg());

        }

        //获取群消息
        CursorPageBaseResp<Message, String> messageCursorPageBaseResp = messageDAO.getCursorPage(req);

        //构建消息
        List<ChatMessageResp> list = messageCursorPageBaseResp.getList().stream()
                .map(message -> {
                    Long fromUid = message.getFromUid();
                    UserBaseInfo baseUserInfo = userCache.getBaseUserInfoByUid(fromUid);
                    IpInfo ipInfo = Optional.ofNullable(baseUserInfo.getIpInfo()).orElse(new IpInfo());
                    IpDetail ipDetail = Optional.ofNullable(ipInfo.getUpdateIpDetail()).orElse(new IpDetail());
                    //构建用户信息
                    ChatMessageResp.UserInfo fromUser = ChatMessageResp.UserInfo.builder()
                            .uid(fromUid)
                            .place(ipDetail.getCity())
                            .build();

                    //构建消息信息
                    AbstractMessageHandler<Object> handler = MessageHandlerFactory.getStrategyNoNull(message.getType());

                    Object body = handler.buildResponseBody(message);

                    ChatMessageResp.ReplyMsg replyMsg = messageService.buildReplyMsg(message.getId());
                    ChatMessageResp.Message msg = ChatMessageResp.Message.builder()
                            .id(message.getId())
                            .sendTime(message.getCreateTime())
                            .type(message.getType())
                            .body(body)
                            .reply(replyMsg)
                            .build();
                    return ChatMessageResp.builder()
                            .fromUser(fromUser)
                            .message(msg)
                            .build();
                }).collect(Collectors.toList());
        return MessageAdapter.buildChatMessageRespList(messageCursorPageBaseResp,list);
    }
}
