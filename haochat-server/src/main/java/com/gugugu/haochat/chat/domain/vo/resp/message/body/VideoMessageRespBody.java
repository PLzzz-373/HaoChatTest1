package com.gugugu.haochat.chat.domain.vo.resp.message.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMessageRespBody {
    /**
     *  大小（字节）
     */
    private Long size;
    /**
     * 下载地址
     */
    private String url;
    /**
     * 视频名
     */
    private String videoName;
}
