package com.gugugu.haochat.common.consumer;

import com.gugugu.haochat.chat.dao.GroupMemberDAO;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.dao.RoomDAO;
import com.gugugu.haochat.chat.dao.RoomFriendDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.domain.entity.RoomFriend;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.service.MessageService;
import com.gugugu.haochat.common.constant.MqConstant;
import com.gugugu.haochat.common.event.domain.dto.MsgSendMessageDTO;
import com.gugugu.haochat.common.service.PushService;
import com.gugugu.haochat.websocket.adapter.WsAdapter;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RocketMQMessageListener(consumerGroup = MqConstant.SEND_MSG_GROUP, topic = MqConstant.SEND_MSG_TOPIC)
@Component
public class MessageSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    @Resource
    private MessageDAO messageDao;

    @Resource
    private RoomDAO roomDao;

    @Resource
    private PushService pushService;

    @Resource
    private MessageService messageService;

    @Resource
    private GroupMemberDAO groupMemberDao;

    @Resource
    private RoomFriendDAO roomFriendDao;

    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        Long msgId = dto.getMsgId();
        Message message = messageDao.getById(msgId);
        Long roomId = message.getRoomId();
        Room room = roomDao.getById(roomId);
        ChatMessageResp chatMessageResp = messageService.buildChatMessageResp(msgId, false, true);
        // 2. 判断是否是热点群聊
        if (room.isHotRoom()) {
            // 2.1 热点群聊
            // 推送给所有人消息
            pushService.sendPushMsg(WsAdapter.buildMsgSend(chatMessageResp));
        } else {
            List<Long> uidList;
            if (room.isRoomGroup()) {
                // 2.2 群聊
                uidList = groupMemberDao.getUidListByRoomId(roomId);
            } else {
                // 2.3 单聊
                RoomFriend roomFriend = roomFriendDao.getUidByRoomId(roomId);
                uidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            // 3. 推送房间成员
            pushService.sendPushMsg(WsAdapter.buildMsgSend(chatMessageResp), uidList);
        }
    }

}
