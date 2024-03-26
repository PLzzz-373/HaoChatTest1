package com.gugugu.haochat.user.service;

public interface IpService {

    /**
     * 异步更新用户ip详情
     *
     * @param uid 用户ID
     */
    void refreshIpDetailAsync(Long uid);
}
