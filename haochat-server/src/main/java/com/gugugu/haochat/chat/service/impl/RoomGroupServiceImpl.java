package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.GroupMemberDAO;
import com.gugugu.haochat.chat.dao.RoomGroupDAO;
import com.gugugu.haochat.chat.domain.vo.req.group.UpdateGroupInfoReq;
import com.gugugu.haochat.chat.service.RoomFriendService;
import com.gugugu.haochat.chat.service.RoomGroupService;
import com.gugugu.haochat.common.cache.RoomCache;
import com.gugugu.haochat.common.domain.enums.GroupRoleEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.vo.resp.RoomBaseInfo;
import com.gugugu.haochat.common.utils.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
@Service
public class RoomGroupServiceImpl implements RoomGroupService {
    @Resource
    private RoomCache roomCache;
    @Resource
    private GroupMemberDAO groupMemberDAO;
    @Resource
    private RoomGroupDAO roomGroupDAO;
    @Override
    public Boolean updateGroupInfo(Long uid, UpdateGroupInfoReq updateGroupInfoReq) {
        Long groupId = updateGroupInfoReq.getGroupId();
        String groupName = updateGroupInfoReq.getGroupName();
        if (ObjectUtil.isNotEmpty(updateGroupInfoReq.getGroupName())) {
            AssertUtil.isTrue(groupName.length() >= 3, ChatErrorEnum.NOT_ALLOWED_LT_THREE_CHAR_FOR_GROUP_NAME.getMsg());
            AssertUtil.isTrue(groupName.length() <= 10, ChatErrorEnum.NOT_ALLOWED_GT_TEN_CHAR_FOR_GROUP_NAME.getMsg());
        }

        // 0. 判断是否是大群聊
        RoomBaseInfo roomBaseInfo = roomCache.getRoomBaseInfoById(updateGroupInfoReq.getGroupId());
        AssertUtil.isFalse(roomBaseInfo.getHotFlag() == 1, ChatErrorEnum.NOT_ALLOWED_UPDATE_GROUP_NAME_FOR_HOT_GROUP.getMsg());

        // 1. 判断该用户是否有权限更改群名
        Boolean isMaster = groupMemberDAO.hasAuthority(groupId, uid, Arrays.asList(GroupRoleEnum.MASTER, GroupRoleEnum.ADMIN));
        AssertUtil.isTrue(isMaster, ChatErrorEnum.NO_POWER_FOR_UPDATE_GROUP_NAME_ADMIN.getMsg());

        // 2. 更改信息
        roomGroupDAO.updateGroupInfo(groupId, updateGroupInfoReq);

        return true;
    }
}
