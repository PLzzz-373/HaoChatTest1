package com.gugugu.haochat.common.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupBaseInfo {
    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 群名称
     */
    private String name;

    /**
     * 群头像
     */
    private String avatar;

    /**
     * 群成员
     */
    private List<Long> memberList;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    private Object extJson;

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    private Integer deleteStatus;
}
