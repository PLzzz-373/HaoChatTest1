package com.gugugu.haochat.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.common.cache.ItemCache;
import com.gugugu.haochat.common.domain.enums.IdempotentEnum;
import com.gugugu.haochat.common.domain.enums.YesOrNoEnum;
import com.gugugu.haochat.common.domain.enums.user.ItemTypeEnum;
import com.gugugu.haochat.user.dao.UserBackpackDAO;
import com.gugugu.haochat.user.domain.entity.ItemConfig;
import com.gugugu.haochat.user.domain.entity.UserBackpack;
import com.gugugu.haochat.user.service.UserBackpackService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserBackpackServiceImpl implements UserBackpackService {
    @Resource
    @Lazy
    private UserBackpackServiceImpl userBackpackService;
    @Resource
    private UserBackpackDAO userBackpackDao;
    @Resource
    private ItemCache itemCache;
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    private void doAcquireItem(Long uid, Long itemId, String idempotent) {
        // 判断用户是否已经获取过该物品（幂等判断）
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
        if (ObjectUtil.isNotNull(userBackpack)) {
            // 该用户已经获取过该物品
            return;
        }
        // 判断获取物品的类型是否是徽章类型，因为每人只能获取同一张徽章一次（业务判断）
        ItemConfig itemConfig = itemCache.getById(itemId);
        boolean equals = ItemTypeEnum.BADGE.getType().equals(itemConfig.getType());
        if (equals) {
            // 判断是否
            Integer count = userBackpackDao.getCountByUidItemId(uid, itemId);
            if (count > 0) {
                // 已经有徽章了不发
                return;
            }
        }
        // 发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
