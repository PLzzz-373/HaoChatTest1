package com.gugugu.haochat.user.domain.vo.resp;

import lombok.Data;

@Data
public class UserSearchRespVO {

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 是否是好友
     */
    private Boolean isFriend;
}
