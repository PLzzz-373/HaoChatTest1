package com.gugugu.haochat.chat.domain.vo.req.message.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoundMessageReqBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 大小（字节）
     */
    @NotNull
    private Long size;

    /**
     * 时长（秒）
     */
    @NotNull
    private Integer second;

    /**
     * 下载地址
     */
    @NotBlank
    private String url;
}
