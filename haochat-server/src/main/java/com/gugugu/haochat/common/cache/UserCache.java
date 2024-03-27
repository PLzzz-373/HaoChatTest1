package com.gugugu.haochat.common.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.utils.RedisUtil;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.domain.vo.resp.UserInfoCache;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserCache {
    @Resource
    @Lazy
    private UserDAO userDAO;
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

    public void updateUserInfo(User user) {
        Long uid = user.getId();
        String key = String.format(RedisKeyConst.USER_INFO_STRING, uid);
        String userKey = RedisKeyConst.getKey(key);
        UserBaseInfo userBaseInfo = BeanUtil.toBean(user, UserBaseInfo.class);
        RedisUtil.set(userKey, userBaseInfo);
    }

    public UserBaseInfo getBaseUserInfoByUid(Long uid) {
        // 1. 先尝试从缓存中获取
        String key = String.format(RedisKeyConst.USER_INFO_STRING, uid);
        String userKey = RedisKeyConst.getKey(key);
        UserBaseInfo userBaseInfo = RedisUtil.get(userKey, UserBaseInfo.class);

        if (ObjectUtil.isNotNull(userBaseInfo)) {
            return userBaseInfo;
        }

        // 2. 从数据库中获取数据
        User user = userDAO.getById(uid);
        UserBaseInfo userInfo = BeanUtil.toBean(user, UserBaseInfo.class);

        // 3. 缓存数据
        RedisUtil.set(userKey, userInfo);
        return userInfo;
    }

    public Long getTotalCount() {

        String key = RedisKeyConst.getKey(RedisKeyConst.USER_TOTAL_COUNT_STRING, "");

        Long totalCount = RedisUtil.get(key, Long.class);

        if (ObjectUtil.isNull(totalCount)) {
            List<User> list = userDAO.list();
            RedisUtil.set(key, list.size());
            return (long) list.size();
        }

        return totalCount;
    }

    public List<UserInfoCache> getBatchByUidList(List<Long> uidList) {
        List<UserInfoCache> userInfoCaches = new ArrayList<>();
        uidList.forEach(uid->{
            UserBaseInfo baseInfo = this.getBaseUserInfoByUid(uid);
            UserInfoCache userInfoCache = BeanUtil.toBean(baseInfo, UserInfoCache.class);
            userInfoCaches.add(userInfoCache);
        });
        return userInfoCaches;
    }

    public Boolean isExistUsers(List<Long> list) {
        boolean isExist = true;
        for (Long uid : list) {
            UserBaseInfo userBaseInfo = getBaseUserInfoByUid(uid);
            if (ObjectUtil.isNull(userBaseInfo)) {
                isExist = false;
            }
        }
        return isExist;
    }
}
