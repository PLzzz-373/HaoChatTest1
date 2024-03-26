package com.gugugu.haochat.common.domain.vo.req;

import lombok.Data;

@Data
public class WsBaseReq {
    /**
     * @see com.gugugu.haochat.common.domain.enums.WsReqTypeEnum
     *
     */
    private Integer type;
    private String data;
}
