package com.gugugu.haochat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.user.domain.entity.UserBackpack;
import com.gugugu.haochat.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

@Service
public class UserBackpackDAO extends ServiceImpl<UserBackpackMapper, UserBackpack> {
    public UserBackpack getByIdp(String idempotent) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent, idempotent)
                .one();
    }

    public Integer getCountByUidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .list()
                .size();
    }
}
