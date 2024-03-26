package com.gugugu.haochat.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApplyExtraInfo {

    /**
     * 当申请为加群申请时，带上此参数
     */
    private Long groupId;

}
