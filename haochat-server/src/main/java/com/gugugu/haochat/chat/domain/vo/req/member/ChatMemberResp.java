package com.gugugu.haochat.chat.domain.vo.req.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMemberResp {
    /**
     * 用户ID
     */
    private Long uid;

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
    private Integer roleId;

    /**
     * @see com.gugugu.haochat.common.domain.enums.ChatActiveStatusEnum
     * 当前状态
     */
    private Integer activeStatus;
}
