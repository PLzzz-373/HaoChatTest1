package com.gugugu.haochat.common.event.listener;

import com.gugugu.haochat.chat.dao.ContactDAO;
import com.gugugu.haochat.chat.dao.RoomDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.common.constant.MqConstant;
import com.gugugu.haochat.common.event.MessageSendEvent;
import com.gugugu.haochat.common.utils.MqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
public class MessageSendListener {
    @Resource
    private MqProducer mqProducer;

    @Resource
    private RoomDAO roomDao;

    @Resource
    private ContactDAO contactDao;

    /**
     * 消息路由
     *
     * @param event 事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Message message = event.getMessage();
        mqProducer.sendMessage(MqConstant.SEND_MSG_TOPIC, message.getId());
    }

    /**
     * 消息路由
     *
     * @param event 事件
     */
    @Async
    @EventListener(classes = MessageSendEvent.class)
    public void updateMessage(MessageSendEvent event) {
        // 更新每个房间的最新消息时间（active_time）和最新消息ID (last_msg_id)
        Message message = event.getMessage();
        Long roomId = message.getRoomId();
        Date updateTime = message.getUpdateTime();
        roomDao.updateRoomNewestMsg(roomId, updateTime, message.getId());
        contactDao.updateReadTime(roomId, updateTime);
        log.info("消息发送成功，消息ID：{}", message.getId());
    }
}
