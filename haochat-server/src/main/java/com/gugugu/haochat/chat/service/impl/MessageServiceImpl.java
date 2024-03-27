package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.req.message.RevokeMessageReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.service.MessageService;
import com.gugugu.haochat.chat.service.strategy.msg.AbstractMessageHandler;
import com.gugugu.haochat.chat.service.strategy.msg.factory.MessageHandlerFactory;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.domain.enums.MessageTypeEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.event.MessageSendEvent;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.IpDetail;
import com.gugugu.haochat.user.domain.IpInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public ChatMessageResp buildChatMessageResp(Long msgId, boolean isCreateTime, boolean isRoomId) {
        //构建回复消息对象
        ChatMessageResp.ReplyMsg replyMsg =  this.buildReplyMsg(msgId);
        //根据消息类型获取对应处理器
        Message message = messageDAO.getById(msgId);
        AbstractMessageHandler<Object> handler = MessageHandlerFactory.getStrategyNoNull(message.getType());
        //返回构建消息
        ChatMessageResp.Message.MessageBuilder builder = ChatMessageResp.Message.builder()
                .id(message.getId())
                .sendTime(isCreateTime ? message.getCreateTime() : message.getUpdateTime())
                .type(message.getType())
                .reply(replyMsg);
        if(isRoomId){
            builder.roomId(message.getRoomId());
        }
        //消息对象
        ChatMessageResp.Message msg = handler.buildChatMessageResp(message, builder);
        //用户对象
        UserBaseInfo baseUserInfo =  userCache.getBaseUserInfoByUid(message.getFromUid());

        IpInfo ipInfo = Optional.ofNullable(baseUserInfo.getIpInfo()).orElse(new IpInfo());
        IpDetail ipDetail = Optional.ofNullable(ipInfo.getUpdateIpDetail()).orElse(new IpDetail());
        ChatMessageResp.UserInfo build = ChatMessageResp.UserInfo.builder()
                .uid(message.getFromUid())
                .place(ipDetail.getCity())
                .build();
        return ChatMessageResp.builder()
                .message(msg)
                .fromUser(build).build();
    }

    @Override
    public ChatMessageResp.ReplyMsg buildReplyMsg(Long msgId) {
        Message message = messageDAO.getById(msgId);
        Long replyMessageId = message.getReplyMessageId();
        ChatMessageResp.ReplyMsg replyMsg;
        if(ObjUtil.isNull(replyMessageId)){
            replyMsg = null;
        }else {
            Message replyMessage = messageDAO.getById(replyMessageId);
            String name = userDAO.getById(replyMessage.getFromUid()).getName();
            Object body;

            //判断回复消息是否撤回
            if(MessageTypeEnum.REVOKE.getType().equals(replyMessage.getType())){
                body = "原消息已撤回";

            }else {
                body = MessageHandlerFactory.getStrategyNoNull(replyMessage.getType()).showInReplyMessage(replyMessage);
            }
            replyMsg = ChatMessageResp.ReplyMsg
                    .builder()
                    .id(replyMessage.getId())
                    .uid(replyMessage.getFromUid())
                    .name(name)
                    .type(replyMessage.getType())
                    .body(body)
                    .canCallback(0)
                    .gapCount(replyMessage.getGapCount())
                    .build();
        }
        return replyMsg;
    }

    @Override
    public void check(Long uid, ChatMessageReq req) {
        //检查回复消息
        this.checkReplyMessage(req);
        //根据类型获取对应的消息处理器
        Integer messageType = req.getMessageType();
        AbstractMessageHandler<Object> handler = MessageHandlerFactory.getStrategyNoNull(messageType);
        //对不同消息进行校验
        handler.checkMessage(req, uid);
    }

    @Override
    public void checkReplyMessage(ChatMessageReq req) {
        Long roomId = req.getRoomId();
        Long replyMessageId = req.getReplyMessageId();
        if(replyMessageId != null){
            //判断回复的消息是否存在
            Message message = messageDAO.getById(replyMessageId);
            AssertUtil.isNotEmpty(message, ChatErrorEnum.MESSAGE_NOT_EXIST.getMsg());
            //判断回复和发送的消息是否在一个房间内
            Long replyMsgRoomId = message.getRoomId();
            AssertUtil.equal(roomId,replyMsgRoomId,ChatErrorEnum.REPLY_MESSAGE_NOT_MATCH.getMsg());
        }
    }

    @Override
    public void save(Message message, ChatMessageReq req) {
        // 1. 根据消息类型获取相应的处理器，对不同消息进行处理
        Integer messageType = req.getMessageType();
        AbstractMessageHandler<Object> handler = MessageHandlerFactory.getStrategyNoNull(messageType);
        // 2. 对不同的消息进行消息入库
        handler.saveMessage(message, req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean revoke(Long uid, RevokeMessageReq req) {
        Long id = req.getMsgId();

        // 1. 判断用户是否有权利撤回该消息
        UserBaseInfo baseInfo = userCache.getBaseUserInfoByUid(uid);
        Boolean isOwner = messageDAO.hasPower(baseInfo, id);
        AssertUtil.isTrue(isOwner, ChatErrorEnum.NOT_ALLOWED_REVOKE.getMsg());

        // 2. 判断发消息时间是否大于三分钟
        Message msg = messageDAO.getById(req.getMsgId());
        AssertUtil.isNotEmpty(msg, ChatErrorEnum.MESSAGE_NOT_EXIST.getMsg());
        Date sendTime = msg.getCreateTime();
        AssertUtil.isFalse(DateUtil.between(sendTime, new Date(), DateUnit.MINUTE) > 3, ChatErrorEnum.NOT_ALLOWED_TO_REVOKE_MESSAGE_OVER_THREE_MINUTES.getMsg());

        // 3. 撤回消息
        Message message = messageDAO.revoke(baseInfo, id);

        // 4. 推送消息
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, message));
        return true;
    }
}
