package com.gugugu.haochat.user.service;

import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.user.domain.vo.resp.BadgeBatchResp;

import java.util.List;

public interface ItemConfigService {
    List<BadgeBatchResp> listBadge(Long uid, PageReq<Object> pageReq);
}
