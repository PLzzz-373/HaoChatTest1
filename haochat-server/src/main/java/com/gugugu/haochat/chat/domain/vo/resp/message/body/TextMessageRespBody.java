package com.gugugu.haochat.chat.domain.vo.resp.message.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gugugu.haochat.common.discover.domain.UrlInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextMessageRespBody {
    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息链接映射
     */
    private Map<String, UrlInfo> urlContentMap;

    /**
     * 艾特的uid
     */
    private List<Long> atUidList;
}
