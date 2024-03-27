package com.gugugu.haochat.chat.domain.vo.req.group;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class InvitAddGroupReq {
    /**
     * 群ID
     */
    @NotNull(message = "群ID不能为空")
    private Long groupId;

    /**
     * 备注
     */
    @NotNull(message = "备注不能为空")
    private String msg;

    /**
     * 邀请用户的列表
     */
    @NotNull(message = "邀请成员不能为空")
    private List<Long> uidList;
}
