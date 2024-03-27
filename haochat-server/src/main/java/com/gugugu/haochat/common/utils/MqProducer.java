package com.gugugu.haochat.common.utils;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MqProducer {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 发送消息
     *
     * @param topic 发送消息的通道名
     * @param body  消息体
     */
    public void sendMessage(String topic, Object body) {
        Message<Object> build = MessageBuilder.withPayload(body).build();
        rocketMqTemplate.send(topic, build);
    }
}
