package com.gugugu.haochat.websocket.service;

import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import io.netty.channel.Channel;

public interface WebSocketService {
    void connet(Channel channel);

    void disconnect(Channel channel);

    void authorize(Channel channel, String token);

    void sendMsgToOne(Channel channel, WsBaseResp<?> wsBaseResp);

    void scan(Channel channel);

    void logout(Channel channel);

    void scanLoginSuccess(Integer loginCode, User user, String token);

    void subscribeSuccess(Integer code);

    void sendMsgToOne(Long uid, WsBaseResp<?> wsBaseResp);

    void sendMsgToAll(WsBaseResp<?> wsBaseResp, Long skipUid);
}
