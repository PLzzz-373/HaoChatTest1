package com.gugugu.haochat.chat.domain.vo.req.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gugugu.haochat.chat.domain.vo.req.message.body.*;
import com.gugugu.haochat.common.discover.domain.UrlInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * url跳转链接
     */
    private Map<String, UrlInfo> urlContentMap;
    /**
     * 消息撤回详情
     */
    private RecallMessageReqBody recall;
    /**
     * 艾特的uid
     */
    private List<Long> atUidList;
    /**
     * 文件消息
     */
    private FileMessageReqBody fileMsg;
    /**
     * 图片消息
     */
    private ImageMessageReqBody imageMessageReqBody;
    /**
     * 语音消息
     */
    private SoundMessageReqBody soundMessageReqBody;
    /**
     * 文件消息
     */
    private VideoMessageReqBody videoMessageReqBody;
    /**
     * 表情图片信息
     */
    private EmojisMessageReqBody emojisMessageReqBody;
}
