package com.gugugu.haochat.chat.domain.vo.resp.message.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMessageRespBody {
    /**
     *  大小（字节）
     */
    private Long size;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 下载地址
     */
    private String url;
}
