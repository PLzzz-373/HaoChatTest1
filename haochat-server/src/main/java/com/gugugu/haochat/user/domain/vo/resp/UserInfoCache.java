package com.gugugu.haochat.user.domain.vo.resp;

import lombok.Data;

@Data
public class UserInfoCache {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;
}
