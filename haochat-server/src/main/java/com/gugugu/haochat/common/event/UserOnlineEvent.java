package com.gugugu.haochat.common.event;

import com.gugugu.haochat.common.event.domain.dto.UserOnlineEventParamsDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private final UserOnlineEventParamsDTO userOnlineEventParamsDTO;

    public UserOnlineEvent(Object source, UserOnlineEventParamsDTO userOnlineEventParamsDTO) {
        super(source);
        this.userOnlineEventParamsDTO = userOnlineEventParamsDTO;
    }
}
