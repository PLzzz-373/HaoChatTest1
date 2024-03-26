package com.gugugu.haochat.common.cache;

import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.utils.RedisUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class UserCache {
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKeyConst.getKey(RedisKeyConst.ONLINE_UID_ZET);
        String offlineKey = RedisKeyConst.getKey(RedisKeyConst.OFFLINE_UID_ZET);
        // 移除上线线表
        RedisUtil.zRemove(onlineKey, uid);
        // 更新上线表
        RedisUtil.zAdd(offlineKey, uid, optTime.getTime());
    }

    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKeyConst.getKey(RedisKeyConst.ONLINE_UID_ZET);
        String offlineKey = RedisKeyConst.getKey(RedisKeyConst.OFFLINE_UID_ZET);
        // 移除离线表
        RedisUtil.zRemove(offlineKey, uid);
        // 更新上线表
        RedisUtil.zAdd(onlineKey, uid, optTime.getTime());
    }
}
