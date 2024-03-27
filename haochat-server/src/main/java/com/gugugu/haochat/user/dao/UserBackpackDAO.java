package com.gugugu.haochat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.common.domain.enums.YesOrNoEnum;
import com.gugugu.haochat.user.domain.entity.UserBackpack;
import com.gugugu.haochat.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<UserBackpack> getValidByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
    }

    public void updateStatus(Long uid, Integer itemId, Integer status) {
        lambdaUpdate()
                .eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getItemId, itemId)
                .set(UserBackpack::getStatus,status)
                .update();
    }
}
