package com.gugugu.haochat.common.event.listener;

import com.gugugu.haochat.common.domain.enums.IdempotentEnum;
import com.gugugu.haochat.common.domain.enums.user.ItemEnum;
import com.gugugu.haochat.common.event.UserRegisterEvent;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.service.UserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserRegisterEventListeners {

    @Resource
    private UserBackpackService userBackpackService;

    /**
     * 发送给用户注册成功，送一张改名卡
     *
     * @param event 用户注册事件
     */
    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void addUpdateNameCard(UserRegisterEvent event) {
        User user = event.getUser();
        //送一张改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }
}
