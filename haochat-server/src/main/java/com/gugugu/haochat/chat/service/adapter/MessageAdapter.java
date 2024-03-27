package com.gugugu.haochat.chat.service.adapter;

import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;

import java.util.List;

public class MessageAdapter {

    /**
     * 构建返回对象
     *
     * @param messageCursorPageBaseResp 返回消息
     * @param list                      列表
     * @return 对象
     */
    public static CursorPageBaseResp<ChatMessageResp, String> buildChatMessageRespList(CursorPageBaseResp<Message, String> messageCursorPageBaseResp, List<ChatMessageResp> list) {
        CursorPageBaseResp<ChatMessageResp, String> cursorPageBaseResp = new CursorPageBaseResp<>();
        cursorPageBaseResp.setList(list);
        cursorPageBaseResp.setCursor(messageCursorPageBaseResp.getCursor());
        cursorPageBaseResp.setIsLast(messageCursorPageBaseResp.getIsLast());
        return cursorPageBaseResp;
    }
}