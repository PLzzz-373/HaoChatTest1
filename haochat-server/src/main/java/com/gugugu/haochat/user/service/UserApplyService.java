package com.gugugu.haochat.user.service;

import com.gugugu.haochat.chat.domain.vo.req.message.MessageOperationReq;
import com.gugugu.haochat.chat.domain.vo.resp.message.MessageResp;
import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;

public interface UserApplyService {
    PageRes<MessageResp> listMessage(Long uid, PageReq<String> pageReq);

    Boolean operation(Long uid, MessageOperationReq messageOperationReq);

}
