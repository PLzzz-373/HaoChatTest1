package com.gugugu.haochat.chat.service.adapter;

import com.gugugu.haochat.chat.domain.vo.req.member.ChatMemberResp;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.user.domain.entity.User;

import java.util.List;

public class GroupMemberAdapter {

    /**
     * 构造群成员体
     *
     * @param list 群成员信息
     * @param groupMemberCursorPageBaseResp 群成员列表
     * @return 最终信息
     */
    public static CursorPageBaseResp<ChatMemberResp, String> buildChatMemberCursorPage(List<ChatMemberResp> list, CursorPageBaseResp<User, String> groupMemberCursorPageBaseResp) {
        CursorPageBaseResp<ChatMemberResp, String> chatMemberRespCursorPageBaseResp = new CursorPageBaseResp<>();
        chatMemberRespCursorPageBaseResp.setCursor(groupMemberCursorPageBaseResp.getCursor());
        chatMemberRespCursorPageBaseResp.setIsLast(groupMemberCursorPageBaseResp.getIsLast());
        chatMemberRespCursorPageBaseResp.setList(list);
        chatMemberRespCursorPageBaseResp.setExtraInfo(groupMemberCursorPageBaseResp.getExtraInfo());
        return chatMemberRespCursorPageBaseResp;
    }
}
