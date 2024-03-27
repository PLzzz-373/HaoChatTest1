package com.gugugu.haochat.chat.domain.vo.req.message.body;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Builder
@ToString
public class TextMessageReqBody {

    /**
     * 消息内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "消息内容过长")
    private String content;

    /**
     * 艾特的uid
     */
    @Size(max = 10, message = "一次别艾特这么多人")
    private List<Long> atUidList;
}
