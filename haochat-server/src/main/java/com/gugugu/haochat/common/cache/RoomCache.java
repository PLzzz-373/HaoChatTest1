package com.gugugu.haochat.common.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.RoomDAO;
import com.gugugu.haochat.common.domain.vo.resp.RoomBaseInfo;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RoomCache {

    @Resource
    private RoomDAO roomDao;

    public RoomBaseInfo getRoomBaseInfoById(Long roomId) {
        String key = RedisKeyConst.getKey(RedisKeyConst.ROOM_INFO_STRING, roomId);
        RoomBaseInfo roomMap = RedisUtil.get(key, RoomBaseInfo.class);

        if (ObjectUtil.isNotNull(roomMap)) {
            return roomMap;
        }

        // 查询数据库
        RoomBaseInfo roomBaseInfo = BeanUtil.toBean(roomDao.getById(roomId), RoomBaseInfo.class);
        RedisUtil.set(key, roomBaseInfo);
        return roomBaseInfo;
    }

}
