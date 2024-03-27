package com.gugugu.haochat.websocket.adapter;

import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.common.domain.enums.WsRespTypeEnum;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.websocket.domain.vo.WsLoginVO;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.stereotype.Component;

@Component
public class WsAdapter {

    /**
     * 构建登录返回请求
     *
     * @param wxMpQrCodeTicket 微信登录二维码票据
     * @return WsBaseResp<WsLoginVO>
     */
    public static WsBaseResp<WsLoginVO> buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WsBaseResp<WsLoginVO> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WsRespTypeEnum.LOGIN_URL.getType());
        wsBaseResp.setData(new WsLoginVO().setUrl(wxMpQrCodeTicket.getUrl()));
        return wsBaseResp;
    }

    /**
     * 构建订阅成功返回体
     *
     * @return 订阅成功返回体
     */
    public static WsBaseResp<?> buildSubscribeSuccessResp() {
        WsBaseResp<?> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WsRespTypeEnum.LOGIN_SUBSCRIBE_SUCCESS.getType());
        return wsBaseResp;
    }

    /**
     * 构建登录成功
     *
     * @param user  用户信息
     * @param token token
     * @param power 用户权限
     * @return 消息体
     */
    public static WsBaseResp<WsLoginSuccessMessage> buildLoginSuccessResp(User user, String token, Long power) {
        WsBaseResp<WsLoginSuccessMessage> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WsRespTypeEnum.LOGIN_SUCCESS.getType());
        WsLoginSuccessMessage wsLoginSuccessMessage = WsLoginSuccessMessage.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .power(power)
                .token(token)
                .uid(user.getId())
                .build();
        wsBaseResp.setData(wsLoginSuccessMessage);
        return wsBaseResp;
    }

    /**
     * 构建无效token
     *
     * @return WsBaseResp<WsLoginSuccessMessage> 消息体
     */
    public static WsBaseResp<WsLoginSuccessMessage> buildInvalidateTokenResp() {
        WsBaseResp<WsLoginSuccessMessage> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WsRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }

    public static WsBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WsBaseResp<ChatMessageResp> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WsRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }
}