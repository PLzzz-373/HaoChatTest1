package com.gugugu.haochat.chat.domain.vo.req.group;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateGroupInfoReq {
    /**
     * 群ID
     */
    @NotNull(message = "群ID不能为空")
    private Long groupId;

    /**
     * 群名称
     */
    @NotNull(message = "群名称不能为空")
    private String groupName;

    /**
     * 群头像
     */
    private String groupAvatar;
}
