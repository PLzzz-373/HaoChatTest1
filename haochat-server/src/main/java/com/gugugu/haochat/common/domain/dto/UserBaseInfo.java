package com.gugugu.haochat.common.domain.dto;

import com.gugugu.haochat.user.domain.IpInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseInfo {
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

    /**
     * 角色
     */
    private Long roleId;

    /**
     * 用户ip信息
     */
    private IpInfo ipInfo;

    /**
     * 用户状态
     */
    private Integer activeStatus;
}
