package com.gugugu.haochat.chat.domain.vo.resp.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatContactCursorResp {
    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    /**
     * 是否全员展示的会话 0否 1是
     */
    private Integer hotFlag;

    /**
     * 最新消息
     */
    private String text;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 会话头像
     */
    private String avatar;

    /**
     * 房间最后活跃时间(用来排序)
     */
    private Date activeTime;

    /**
     * 未读数
     */
    private Integer unreadCount;
}
