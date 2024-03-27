package com.gugugu.haochat.chat.dao;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.RoomGroup;
import com.gugugu.haochat.chat.domain.vo.req.group.UpdateGroupInfoReq;
import com.gugugu.haochat.chat.mapper.RoomGroupMapper;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.dto.GroupBaseInfo;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.common.utils.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class RoomGroupDAO extends ServiceImpl<RoomGroupMapper, RoomGroup> {
    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

    public void createGroup(Long roomId, String groupName, String groupAvatar) {
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setRoomId(roomId);
        roomGroup.setName(groupName);
        roomGroup.setAvatar(groupAvatar);
        this.save(roomGroup);
    }

    public void updateGroupInfo(Long groupId, UpdateGroupInfoReq groupInfo) {

        LambdaUpdateWrapper<RoomGroup> wrapper = new UpdateWrapper<RoomGroup>()
                .lambda()
                .eq(RoomGroup::getRoomId, groupId)
                .set(ObjectUtil.isNotNull(groupInfo.getGroupName()), RoomGroup::getName, groupInfo.getGroupName())
                .set(ObjectUtil.isNotNull(groupInfo.getGroupAvatar()), RoomGroup::getAvatar, groupInfo.getGroupAvatar());
        // 更新数据库
        this.update(wrapper);

        // 更新缓存
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, groupId);
        GroupBaseInfo groupBaseInfo = RedisUtil.get(key, GroupBaseInfo.class);
        AssertUtil.isNotEmpty(groupBaseInfo, CommonErrorEnum.SYSTEM_ERROR.getMsg());

        // 更改信息
        if (ObjectUtil.isNotNull(groupInfo.getGroupName())) {
            groupBaseInfo.setName(groupInfo.getGroupName());
        }
        if (ObjectUtil.isNotNull(groupInfo.getGroupAvatar())) {
            groupBaseInfo.setAvatar(groupInfo.getGroupAvatar());
        }
        RedisUtil.set(key, groupBaseInfo);
    }
}
