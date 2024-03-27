package com.gugugu.haochat.common.consumer;

import com.gugugu.haochat.common.constant.MqConstant;
import com.gugugu.haochat.common.domain.dto.PushMessageDTO;
import com.gugugu.haochat.common.domain.enums.WsPushTypeEnum;
import com.gugugu.haochat.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(topic = MqConstant.PUSH_TOPIC, consumerGroup = MqConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    /**
     *
     * 监听消息
     * @param message 消息
     */
    @Override
    public void onMessage(PushMessageDTO message) {
        WsPushTypeEnum wsPushTypeEnum = WsPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case USER :
                webSocketService.sendMsgToOne(message.getUid(), message.getWsBaseMsg());
                break;
            case ALL : webSocketService.sendMsgToAll(message.getWsBaseMsg(), null);
                break;
            default :
                return;
        }
    }
}
