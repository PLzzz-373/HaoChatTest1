package com.gugugu.haochat.common.event.adapter;

import com.gugugu.haochat.common.event.domain.dto.UserOnlineEventParamsDTO;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import io.netty.channel.Channel;

public class EventParamAdapter {

    /**
     * 构建用户上线事件参数
     *
     * @param channel                  当前通道
     * @param wsLoginSuccessWsBaseResp 用户登录参数
     * @return 参数
     */
    public static UserOnlineEventParamsDTO buildUserOnlineEventParams(
            Channel channel,
            WsBaseResp<WsLoginSuccessMessage> wsLoginSuccessWsBaseResp
    ) {
        UserOnlineEventParamsDTO userOnlineEventParamsDTO = new UserOnlineEventParamsDTO();
        userOnlineEventParamsDTO
                .setChannel(channel)
                .setWsLoginSuccessWsBaseResp(wsLoginSuccessWsBaseResp);
        return userOnlineEventParamsDTO;
    }

}
