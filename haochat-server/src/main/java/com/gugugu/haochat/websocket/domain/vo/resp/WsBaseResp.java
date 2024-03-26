package com.gugugu.haochat.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WsBaseResp<T> {
    /**
     * 返回类型
     *
     * @see com.gugugu.haochat.common.domain.enums.WsRespTypeEnum
     */
    private Integer type;

    /**
     * 返回的数据
     */
    private T data;
}
