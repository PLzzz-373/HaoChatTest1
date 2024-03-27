package com.gugugu.haochat.chat.domain.vo.req.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberExtraResp {

    /**
     * 当前响应的用户状态
     */
    private Integer activeStatus;

    /**
     * 群总数
     */
    private Integer totalCount;
}
