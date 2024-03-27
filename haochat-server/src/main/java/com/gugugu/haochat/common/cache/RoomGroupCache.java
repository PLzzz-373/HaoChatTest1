package com.gugugu.haochat.common.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.GroupMemberDAO;
import com.gugugu.haochat.chat.dao.RoomGroupDAO;
import com.gugugu.haochat.chat.domain.entity.RoomGroup;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.dto.GroupBaseInfo;
import com.gugugu.haochat.common.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RoomGroupCache {

    @Resource
    private RoomGroupDAO roomGroupDao;


    /**
     * 通过房间ID获取群信息
     *
     * @param roomId 房间ID
     * @return 群信息
     */
    public GroupBaseInfo getBaseInfoById(Long roomId) {
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, roomId);
        GroupBaseInfo baseInfo = RedisUtil.get(key, GroupBaseInfo.class);

        if (ObjectUtil.isNotNull(baseInfo)) {
            return baseInfo;
        }

        // 查询数据库
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        GroupBaseInfo groupBaseInfo = BeanUtil.toBean(roomGroup, GroupBaseInfo.class);
        this.updateGroupInfoCache(roomId, groupBaseInfo);
        return groupBaseInfo;
    }

    /**
     * 通过房间ID获取群成员id
     *
     * @param roomId 房间ID
     */
    public List<Long> getGroupUidByRoomId(Long roomId) {
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, roomId);
        GroupBaseInfo groupBaseInfo = RedisUtil.get(key, GroupBaseInfo.class);

        if (ObjectUtil.isNotNull(groupBaseInfo)) {
            return groupBaseInfo.getMemberList();
        }
        // 更新数据库
        return this.getBaseInfoById(roomId).getMemberList();
    }

    /**
     * 更新缓存
     *
     * @param groupId       群ID
     * @param groupBaseInfo 群信息
     */
    public void updateGroupInfoCache(Long groupId, GroupBaseInfo groupBaseInfo) {
        // 更新缓存
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, groupId);
        RedisUtil.set(key, groupBaseInfo);
    }
}
