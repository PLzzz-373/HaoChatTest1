package com.gugugu.haochat.wechat.adapter;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public class WechatTextBuilderAdapter {

    /**
     * @param content   需要返回给用户的内容
     * @param wxMessage 微信消息对象
     * @return {@link WxMpXmlOutMessage}
     */
    public WxMpXmlOutMessage build(
            String content,
            WxMpXmlMessage wxMessage
    ) {
        return WxMpXmlOutMessage
                .TEXT()
                .content(content)
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }
}
