package com.gugugu.haochat.user.service;

import com.gugugu.haochat.common.domain.vo.req.PageReq;
import com.gugugu.haochat.user.domain.vo.resp.PageRes;
import com.gugugu.haochat.user.domain.vo.resp.UserInfoCache;
import com.gugugu.haochat.user.domain.vo.resp.UserSearchRespVO;

import java.util.List;

public interface UserService {
    void register(String openId);

    Boolean updateUsername(Long uid, String username);

    List<UserInfoCache> getBatchUserInfoCache(List<Long> list);

    PageRes<UserSearchRespVO> search(Long uid, PageReq<String> pageReq);
}
