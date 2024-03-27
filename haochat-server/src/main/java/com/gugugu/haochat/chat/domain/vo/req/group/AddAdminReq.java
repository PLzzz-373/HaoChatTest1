package com.gugugu.haochat.chat.domain.vo.req.group;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddAdminReq {
    /**
     * 群ID
     */
    @NotNull(message = "群ID不能为空")
    private Long groupId;

    /**
     * 邀请用户的列表
     */
    @NotEmpty(message = "成员列表不能为空")
    private List<Long> uidList;
}
