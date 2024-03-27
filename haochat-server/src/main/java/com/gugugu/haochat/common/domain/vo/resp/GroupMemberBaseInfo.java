package com.gugugu.haochat.common.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberBaseInfo {

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 角色ID
     */
    private Integer role;

}
