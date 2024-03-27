package com.gugugu.haochat.chat.domain.vo.resp.message.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageMessageRespBody {
    /**
     * 大小（字节）
     */
    private Long size;

    /**
     *  宽度（像素）
     */
    private Integer width;

    /**
     * 高度（像素）
     */
    private Integer height;

    /**
     * 下载地址
     */
    private String url;
}
