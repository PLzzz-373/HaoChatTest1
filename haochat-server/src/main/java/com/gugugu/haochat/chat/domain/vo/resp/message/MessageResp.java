package com.gugugu.haochat.chat.domain.vo.resp.message;

import lombok.Data;

@Data
public class MessageResp {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息的发送者ID
     */
    private Long uid;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 备注
     */
    private String msg;

    /**
     * 消息类型 1未读 2已读
     */
    private Integer type;

}
