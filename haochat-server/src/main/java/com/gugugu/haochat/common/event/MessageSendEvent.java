package com.gugugu.haochat.common.event;

import com.gugugu.haochat.chat.domain.entity.Message;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageSendEvent extends ApplicationEvent {
    private final Message message;

    public MessageSendEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }
}
