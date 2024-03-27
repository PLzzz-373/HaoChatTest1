package com.gugugu.haochat.common.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.RoomFriendDAO;
import com.gugugu.haochat.chat.domain.entity.RoomFriend;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.vo.resp.FriendBaseInfo;
import com.gugugu.haochat.common.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RoomFriendCache {

    @Resource
    private RoomFriendDAO roomFriendDao;

    /**
     * 通过房间ID获取单聊信息
     *
     * @param roomId 房间ID
     * @return 单聊信息
     */
    public FriendBaseInfo getBaseInfoById(Long roomId) {
        String key = RedisKeyConst.getKey(RedisKeyConst.FRIEND_INFO_STRING, roomId);
        FriendBaseInfo baseInfo = RedisUtil.get(key, FriendBaseInfo.class);

        if (ObjectUtil.isNotNull(baseInfo)) {
            return baseInfo;
        }

        // 查询数据库
        RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
        FriendBaseInfo friendBaseInfo = BeanUtil.toBean(roomFriend, FriendBaseInfo.class);
        RedisUtil.set(key, friendBaseInfo);
        return friendBaseInfo;
    }

}
