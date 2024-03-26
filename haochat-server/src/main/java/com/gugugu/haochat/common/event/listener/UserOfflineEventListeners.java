package com.gugugu.haochat.common.event.listener;

import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.enums.ChatActiveStatusEnum;
import com.gugugu.haochat.common.event.UserOfflineEvent;

import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class UserOfflineEventListeners {
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserCache userCache;

    /**
     * 更新缓存层数据库
     *
     * @param event 用户下线事件参数
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateCacheDB(UserOfflineEvent event) {
        Long uid = event.getUid();
        // 在redis缓存中删除那个uid
        userCache.offline(uid, new Date());
    }

    /**
     * 更新持久层数据库
     *
     * @param event 用户下线事件参数
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updatePersistenceDB(UserOfflineEvent event) {
        Long uid = event.getUid();
        User user = new User();
        user.setId(uid);
        user.setLastOptTime(new Date());
        user.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDAO.updateById(user);
    }
}