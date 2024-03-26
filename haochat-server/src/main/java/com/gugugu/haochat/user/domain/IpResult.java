package com.gugugu.haochat.user.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class IpResult<T> implements Serializable {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 返回对象
     */
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
