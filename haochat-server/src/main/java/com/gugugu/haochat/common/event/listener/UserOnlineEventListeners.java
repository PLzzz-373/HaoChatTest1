package com.gugugu.haochat.common.event.listener;

import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.enums.ChatActiveStatusEnum;
import com.gugugu.haochat.common.event.UserOnlineEvent;
import com.gugugu.haochat.common.event.domain.dto.UserOnlineEventParamsDTO;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.service.IpService;
import com.gugugu.haochat.websocket.constant.AuthorizationConst;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import com.gugugu.haochat.websocket.service.WebSocketService;
import com.gugugu.haochat.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class UserOnlineEventListeners {

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserDAO userDao;

    @Resource
    private IpService ipService;

    @Resource
    private UserCache userCache;

    /**
     * 发送给用户登录成功
     *
     * @param event 用户上线事件参数
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void sendMsgToOne(UserOnlineEvent event) {
        UserOnlineEventParamsDTO userOnlineEventParamsDTO = event.getUserOnlineEventParamsDTO();
        Channel channel = userOnlineEventParamsDTO.getChannel();
        WsBaseResp<WsLoginSuccessMessage> wsLoginSuccessWsBaseResp = userOnlineEventParamsDTO.getWsLoginSuccessWsBaseResp();
        webSocketService.sendMsgToOne(channel, wsLoginSuccessWsBaseResp);
    }

    /**
     * 更新缓存层数据库
     *
     * @param event 用户上线事件参数
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void updateCacheDB(UserOnlineEvent event) {
        UserOnlineEventParamsDTO userOnlineEventParamsDTO = event.getUserOnlineEventParamsDTO();
        Long uid = userOnlineEventParamsDTO.getWsLoginSuccessWsBaseResp().getData().getUid();
        // 更新用户上线信息
        userCache.online(uid, new Date());
    }

    /**
     * 更新持久层数据库
     *
     * @param event 用户上线事件参数
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void updatePersistenceDB(UserOnlineEvent event) {
        UserOnlineEventParamsDTO userOnlineEventParamsDTO = event.getUserOnlineEventParamsDTO();
        Long uid = userOnlineEventParamsDTO.getWsLoginSuccessWsBaseResp().getData().getUid();
        User user = userDao.getById(uid);
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        user.refreshIp(NettyUtil.getAttrFromChannel(userOnlineEventParamsDTO.getChannel(), AuthorizationConst.IP_KEY_IN_CHANNEL));
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }
}
