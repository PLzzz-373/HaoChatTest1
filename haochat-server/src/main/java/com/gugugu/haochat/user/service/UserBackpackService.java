package com.gugugu.haochat.user.service;

import com.gugugu.haochat.common.domain.enums.IdempotentEnum;

public interface UserBackpackService {
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
