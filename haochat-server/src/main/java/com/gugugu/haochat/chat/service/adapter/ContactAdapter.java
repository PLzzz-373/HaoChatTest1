package com.gugugu.haochat.chat.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ChatContactCursorResp;
import com.gugugu.haochat.chat.domain.vo.resp.contact.ContactWithActiveMsg;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;
import java.util.List;

public class ContactAdapter {
    /**
     * 构建
     *
     * @param list       列表
     * @param cursorPage 游标对象
     * @return 返回响应
     */
    public static CursorPageBaseResp<ChatContactCursorResp, Date> buildContactCursorPage(List<ChatContactCursorResp> list, CursorPageBaseResp<ContactWithActiveMsg, Date> cursorPage) {
        CursorPageBaseResp<ChatContactCursorResp, Date> chatMemberRespCursorPageBaseResp = new CursorPageBaseResp<>();
        chatMemberRespCursorPageBaseResp.setCursor(cursorPage.getCursor());
        chatMemberRespCursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        chatMemberRespCursorPageBaseResp.setList(list);
        if (ObjectUtil.isNotNull(cursorPage.getExtraInfo())) {
            chatMemberRespCursorPageBaseResp.setExtraInfo(cursorPage.getExtraInfo());
        }
        return chatMemberRespCursorPageBaseResp;
    }
}
