package com.gugugu.haochat.common.event.domain.dto;

import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class UserOnlineEventParamsDTO {
    /**
     * 通道
     */
    private Channel channel;

    /**
     * 用户登录成功体
     */
    private WsBaseResp<WsLoginSuccessMessage> wsLoginSuccessWsBaseResp;
}
