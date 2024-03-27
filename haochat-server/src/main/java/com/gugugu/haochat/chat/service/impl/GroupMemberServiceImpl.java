package com.gugugu.haochat.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.*;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.domain.vo.req.group.AddAdminReq;
import com.gugugu.haochat.chat.domain.vo.req.group.CreateGroupReq;
import com.gugugu.haochat.chat.domain.vo.req.group.DelAdminReq;
import com.gugugu.haochat.chat.domain.vo.req.group.InvitAddGroupReq;
import com.gugugu.haochat.chat.service.GroupMemberService;
import com.gugugu.haochat.chat.service.RoomService;
import com.gugugu.haochat.common.cache.RoomGroupCache;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.constant.GroupConst;
import com.gugugu.haochat.common.domain.dto.GroupBaseInfo;
import com.gugugu.haochat.common.domain.enums.ChatMessageEnum;
import com.gugugu.haochat.common.domain.enums.GroupRoleEnum;
import com.gugugu.haochat.common.domain.enums.UserMessageEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.UserErrorEnum;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.user.dao.UserApplyDAO;
import com.gugugu.haochat.user.domain.entity.UserApply;
import com.gugugu.haochat.user.domain.vo.resp.UserSearchRespVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    @Resource
    private GroupMemberDAO groupMemberDAO;
    @Resource
    private RoomDAO roomDAO;
    @Resource
    private ContactDAO contactDAO;
    @Resource
    private MessageDAO messageDAO;
    @Resource
    private UserCache userCache;
    @Resource
    private RoomFriendDAO roomFriendDAO;
    @Resource
    private UserApplyDAO userApplyDAO;
    @Resource
    private RoomService roomService;
    @Resource
    private RoomGroupCache roomGroupCache;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exitGroup(Long uid, Long groupId) {
        Boolean isGroupShip = groupMemberDAO.isGroupShip(groupId, Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, ChatErrorEnum.NOT_IN_GROUP.getMsg());

        Room room = roomDAO.getById(groupId);
        if (room.isHotRoom()) {
            return ChatErrorEnum.NOT_ALLOWED_TO_EXIT_ALL_GROUP.getErrorMsg();
        }

        // 判断当前退出的人是否是群主
        Boolean isMaster = groupMemberDAO.hasAuthority(groupId, uid, Collections.singletonList(GroupRoleEnum.MASTER));
        if (isMaster) {
            // 删房间
            roomDAO.deleteById(groupId);
            // 删会话
            contactDAO.deleteByRoomId(groupId);
            // 删消息
            messageDAO.deleteByRoomId(groupId);
            // 删成员
            groupMemberDAO.deleteByRoomId(groupId);
            return ChatMessageEnum.DEL_GROUP_SUCCESS.getMsg();
        } else {
            // 删除成员
            Boolean isExitGroup = groupMemberDAO.exitGroup(groupId, uid);
            AssertUtil.isTrue(isExitGroup, CommonErrorEnum.SYSTEM_ERROR.getMsg());

            // 删除会话记录
            Boolean isDelContact = contactDAO.delContact(groupId, uid);
            AssertUtil.isTrue(isDelContact, CommonErrorEnum.SYSTEM_ERROR.getMsg());

            return ChatMessageEnum.EXIT_GROUP_SUCCESS.getMsg();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createGroup(Long uid, CreateGroupReq createGroupReq) {
        List<Long> uidList = createGroupReq.getUidList();

        String msg = createGroupReq.getMsg();

        // 0. 判断uidList是否为空
        AssertUtil.isFalse(uidList.size() ==0, ChatErrorEnum.EMPTY_LIST.getMsg());

        // 1. 判断用户是否存在
        Boolean isExistUsers = userCache.isExistUsers(uidList);
        AssertUtil.isTrue(isExistUsers, UserErrorEnum.USER_NOT_EXIST.getMsg());

        // 2. 判断是否已经是好友关系
        List<UserSearchRespVO> friend = roomFriendDAO.isFriend(uid, uidList);
        friend.forEach(user -> {
            AssertUtil.isTrue(user.getIsFriend(), UserErrorEnum.NOT_FRIEND.getMsg());
        });

        // 3. 检查人数是否达到上限
        int uidCount = createGroupReq.getUidList().size();
        AssertUtil.isTrue(uidCount + 1 < GroupConst.MAX_COUNT_PER_GROUP, ChatErrorEnum.MAX_COUNT_PER_GROUP_LIMIT.getMsg());

        // 4. 判断是否已经达到建群上限
        Long count = groupMemberDAO.getCreateGroupCountByUid(uid);
        AssertUtil.isTrue(count < GroupConst.MAX_CREATE_GROUP_COUNT, ChatErrorEnum.CREATE_GROUP_MAX_COUNT.getMsg());

        // 5. 创建群聊
        Long groupId = roomService.createGroup(uid, createGroupReq.getGroupName(), createGroupReq.getGroupAvatar());

        // 6. 提交申请
        Boolean isAddFriend = userApplyDAO.applyCreateGroup(groupId, uid, uidList, msg);
        AssertUtil.isTrue(isAddFriend, UserErrorEnum.COMMIT_APPLY_FAIL.getMsg());

        return UserMessageEnum.COMMIT_APPLY_SUCESS.getMsg();
    }

    @Override
    public Boolean inviteGroup(Long uid, InvitAddGroupReq invitAddGroupReq) {
        // -1. 判断邀请好友列表是否为空
        AssertUtil.isFalse(invitAddGroupReq.getUidList().size() <= 0, ChatErrorEnum.EMPTY_LIST.getMsg());

        // 0. 判断群是否存在
        Long groupId = invitAddGroupReq.getGroupId();
        GroupBaseInfo baseInfo = roomGroupCache.getBaseInfoById(groupId);
        AssertUtil.isFalse(ObjectUtil.isNull(baseInfo), ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        // 1. 判断该用户是否有权限邀请人加群
        Boolean hasPower = groupMemberDAO.hasPowerForInviteGroup(groupId, uid);
        AssertUtil.isTrue(hasPower, ChatErrorEnum.EXIST_GROUP_MEMBER.getMsg());

        // 2. 判断是否达到群人数上限
        List<Long> uidList = roomGroupCache.getGroupUidByRoomId(groupId);
        uidList.addAll(invitAddGroupReq.getUidList());
        Set<Long> uidListDistinct = new HashSet<>(uidList);
        AssertUtil.isTrue(uidListDistinct.size() <= GroupConst.MAX_COUNT_PER_GROUP, ChatErrorEnum.MAX_COUNT_PER_GROUP_LIMIT.getMsg());

        // 3. 发出加群邀请
        Boolean isInvited = userApplyDAO.inviteGroup(uid, groupId, invitAddGroupReq.getUidList(), invitAddGroupReq.getMsg());
        AssertUtil.isTrue(isInvited, ChatErrorEnum.EXIST_GROUP_MEMBER.getMsg());
        return true;

    }

    @Override
    public Boolean addAdmin(Long uid, AddAdminReq addAdminReq) {
        Long groupId = addAdminReq.getGroupId();
        List<Long> uidList = addAdminReq.getUidList();

        // 1. 判断权限身份
        Boolean isMaster = groupMemberDAO.hasAuthority(groupId, uid, Collections.singletonList(GroupRoleEnum.MASTER));
        AssertUtil.isTrue(isMaster, ChatErrorEnum.NO_POWER_FOR_ADD_ADMIN.getMsg());

        // 2. 判断成员列表是否是群中成员
        Boolean isGroupShip = groupMemberDAO.isGroupShip(groupId, uidList);
        AssertUtil.isTrue(isGroupShip, ChatErrorEnum.NOT_ALLOWED_ADD_ADMIN_WITH_NOT_MEMBER.getMsg());

        // 3. 判断管理员uidList
        List<Long> adminUidList = groupMemberDAO.getAdminCount(groupId);

        List<Long> newAdminUidList = new ArrayList<>(adminUidList);
        newAdminUidList.addAll(uidList);
        adminUidList = newAdminUidList;
        Set<Long> uidSet = new HashSet<>(adminUidList);
        AssertUtil.isTrue(uidSet.size() <= GroupConst.MAX_ADMIN_COUNT_PER_GROUP, ChatErrorEnum.MAX_ADMIN_COUNT_LIMIT.getMsg());

        // 4. 添加管理员
        groupMemberDAO.setAdmin(groupId, uidList);

        return true;
    }

    @Override
    public Boolean delAdmin(Long uid, DelAdminReq delAdminReq) {
        Long groupId = delAdminReq.getGroupId();
        List<Long> uidList = delAdminReq.getUidList();

        // 1. 判断权限身份
        Boolean isMaster = groupMemberDAO.hasAuthority(groupId, uid, Collections.singletonList(GroupRoleEnum.MASTER));
        AssertUtil.isTrue(isMaster, ChatErrorEnum.NO_POWER_FOR_DEL_ADMIN.getMsg());

        // 2. 判断成员列表是否是群中成员
        Boolean isGroupShip = groupMemberDAO.isGroupShip(groupId, uidList);
        AssertUtil.isTrue(isGroupShip, CommonErrorEnum.SYSTEM_ERROR.getMsg());

        // 3. 判断管理员uidList
        List<Long> adminUidList = groupMemberDAO.getAdminCount(groupId);
        AssertUtil.isFalse(adminUidList.size() == 0, ChatErrorEnum.ADMIN_NOT_EXIST.getMsg());

        // 4. 移除管理员
        groupMemberDAO.delAdmin(groupId, uidList);

        return true;
    }

    @Override
    public void agreeAddGroup(UserApply userApply) {
        // 0. 判断群是否存在
        Long groupId = userApply.getExtraInfo().getGroupId();
        Room room = roomDAO.getById(groupId);
        AssertUtil.isFalse(ObjectUtil.isNull(room), ChatErrorEnum.ROOM_NOT_EXIST.getMsg());

        // 1. 判断是否已经是群成员
        Boolean isGroupShip = groupMemberDAO.isGroupShip(groupId, Collections.singletonList(userApply.getTargetId()));
        AssertUtil.isFalse(isGroupShip, ChatErrorEnum.EXIST_GROUP_MEMBER.getMsg());

        // 2. 判断是否达到群人数上限
        List<Long> uidList = groupMemberDAO.getUidListByRoomId(groupId);
        AssertUtil.isTrue(uidList.size() <= GroupConst.MAX_COUNT_PER_GROUP - 1, ChatErrorEnum.MAX_COUNT_PER_GROUP_LIMIT.getMsg());

        // 3. 加群
        this.addGroup(groupId, Collections.singletonList(userApply.getTargetId()));
    }

    private void addGroup(Long groupId, List<Long> uidList) {
        // 1. 加群
        groupMemberDAO.addGroupMember(groupId, uidList);

        // 2. 创建会话
        contactDAO.createContactBatch(groupId, uidList, new Date());
    }
}
