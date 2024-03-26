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
public class ImageMessageReqBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 大小（字节）
     */
    @NotNull
    private Long size;

    /**
     * 宽度（像素）
     */
    @NotNull
    private Integer width;

    /**
     * 高度（像素）
     */
    @NotNull
    private Integer height;

    /**
     * 下载地址
     */
    @NotBlank
    private String url;
}
