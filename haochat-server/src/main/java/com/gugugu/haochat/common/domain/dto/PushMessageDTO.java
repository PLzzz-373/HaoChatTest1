package com.gugugu.haochat.common.domain.dto;

import com.gugugu.haochat.common.domain.enums.WsPushTypeEnum;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageDTO implements Serializable {
    /**
     * 推送的ws消息
     */
    private WsBaseResp<?> wsBaseMsg;

    /**
     * 推送的uid
     */
    private Long uid;

    /**
     * 推送类型 1个人 2全员
     *
     * @see com.gugugu.haochat.common.domain.enums.WsPushTypeEnum
     */
    private Integer pushType;

    public PushMessageDTO(Long uid, WsBaseResp<?> wsBaseMsg) {
        this.uid = uid;
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WsPushTypeEnum.USER.getType();
    }

    public PushMessageDTO(WsBaseResp<?> wsBaseMsg) {
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WsPushTypeEnum.ALL.getType();
    }
}
