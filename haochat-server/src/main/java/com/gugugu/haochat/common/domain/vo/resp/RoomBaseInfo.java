package com.gugugu.haochat.common.domain.vo.resp;

import lombok.Data;

@Data
public class RoomBaseInfo {
    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    private Integer hotFlag;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    private Object extJson;
}
