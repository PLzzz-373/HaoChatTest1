package com.gugugu.haochat.common.domain.vo.resp;

import lombok.Data;

@Data
public class FriendBaseInfo {
    /**
     * 房间id
     */
    private Long roomId;

    /**
     * uid1（更小的uid）
     */
    private Long uid1;

    /**
     * uid2（更大的uid）
     */
    private Long uid2;

    /**
     * 房间key由两个uid拼接，先做排序uid1_uid2
     */
    private String roomKey;

    /**
     * 房间状态 0正常 1禁用(删好友了禁用)
     */
    private Integer status;
}
