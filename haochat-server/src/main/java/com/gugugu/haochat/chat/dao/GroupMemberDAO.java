package com.gugugu.haochat.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.GroupMember;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.mapper.GroupMemberMapper;
import com.gugugu.haochat.chat.service.RoomService;
import com.gugugu.haochat.common.cache.RoomGroupCache;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.dto.GroupBaseInfo;
import com.gugugu.haochat.common.domain.enums.GroupRoleEnum;
import com.gugugu.haochat.common.domain.enums.error.ChatErrorEnum;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.domain.vo.resp.GroupMemberBaseInfo;
import com.gugugu.haochat.common.utils.AssertUtil;
import com.gugugu.haochat.common.utils.RedisUtil;
import com.gugugu.haochat.user.dao.UserRoleDAO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupMemberDAO extends ServiceImpl<GroupMemberMapper, GroupMember> {
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    @Lazy
    private RoomService roomService;
    @Resource
    private RoomDAO roomDAO;
    @Resource
    private UserRoleDAO userRoleDAO;
    public List<Long> getUidListByRoomId(Long roomId) {
        return roomGroupCache.getGroupUidByRoomId(roomId);
    }

    public Boolean isGroupShip(Long roomId, List<Long> uids) {
        // 1. 通过ID查询房间内的所有成员ID
        List<Long> uidList = roomGroupCache.getGroupUidByRoomId(roomId);
        // 2. 判断每个成员是否在这个集合中
        for (Long uid : uids) {
            boolean isContains = uidList.contains(uid);
            if (!isContains) {
                return false;
            }
        }
        return true;
    }

    public Boolean hasAuthority(Long roomId, Long uid, List<GroupRoleEnum> authorities) {
        Boolean isSameRoom = roomService.checkRoomMembership(roomId,uid);
        AssertUtil.isTrue(isSameRoom, ChatErrorEnum.NOT_IN_GROUP.getMsg());
        //判断权限
        Room room = roomDAO.getById(roomId);
        if(room.isHotRoom()){
            //大群聊
            return userRoleDAO.hasAuthorities(uid);
        }else {
            //普通群聊
            return this.hasAuthorities(roomId, uid, authorities);
        }
    }

    private Boolean hasAuthorities(Long roomId, Long uid, List<GroupRoleEnum> authorities) {
        //判断权限列表是否为空
        boolean empty = CollectionUtil.isEmpty(authorities);
        if(empty){
            return true;
        }
        //判断用户是否有权限
        LambdaQueryChainWrapper<GroupMember> select = lambdaQuery()
                .eq(GroupMember::getRoomId, roomId)
                .eq(GroupMember::getUid, uid)
                .select(GroupMember::getRole);
        GroupMember groupMember = this.getOne(select);
        Integer role = groupMember.getRole();
        return authorities.contains(GroupRoleEnum.of(role));
    }

    public Integer getRoleId(Long roomId, Long uid) {
        return this.lambdaQuery()
                .eq(GroupMember::getRoomId, roomId)
                .eq(GroupMember::getUid, uid)
                .select(GroupMember::getRole)
                .one()
                .getRole();
    }

    public void deleteByRoomId(Long roomId) {
        LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                .lambda()
                .eq(GroupMember::getRoomId, roomId);
        this.remove(wrapper);
    }

    public Boolean exitGroup(Long groupId, Long uid) {
        // 删库
        QueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                .eq("room_id", groupId)
                .eq("uid", uid);

        // 更新缓存
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, groupId);
        GroupBaseInfo groupBaseInfo = RedisUtil.get(key, GroupBaseInfo.class);
        List<Long> list = groupBaseInfo.getMemberList().stream().filter(id -> BooleanUtil.isFalse(id.equals(uid))).collect(Collectors.toList());
        groupBaseInfo.setMemberList(list);
        RedisUtil.set(key, groupBaseInfo);
        return this.remove(wrapper);
    }

    public Long getCreateGroupCountByUid(Long uid) {
        return this.lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.MASTER.getId())
                .count();
    }
    @Transactional(rollbackFor = Exception.class)
    public void createGroup(Long roomId, String groupName, List<GroupMemberBaseInfo> list) {
        List<GroupMember> saveBatchList = new ArrayList<>();
        list.forEach(groupMemberBaseInfo -> {
            GroupMember groupMember = new GroupMember();
            groupMember.setRoomId(roomId);
            groupMember.setUid(groupMemberBaseInfo.getUid());
            groupMember.setRole(groupMemberBaseInfo.getRole());
            saveBatchList.add(groupMember);
        });
        List<Long> uidList = saveBatchList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        // 设置缓存
        GroupBaseInfo groupBaseInfo = new GroupBaseInfo();
        groupBaseInfo.setRoomId(roomId);
        groupBaseInfo.setName(groupName);
        groupBaseInfo.setMemberList(uidList);
        roomGroupCache.updateGroupInfoCache(roomId, groupBaseInfo);

        // 更新缓存中的成员列表
        this.addGroupMember(roomId, uidList);
        this.saveBatch(saveBatchList);
    }
    @Transactional(rollbackFor = Exception.class)
    public void addGroupMember(Long groupId, List<Long> uidList) {
        List<GroupMember> saveBatchList = new ArrayList<>();
        uidList.forEach(uid -> {
            GroupMember groupMember = new GroupMember();
            groupMember.setRoomId(groupId);
            groupMember.setUid(uid);
            groupMember.setRole(GroupRoleEnum.MEMBER.getId());
            saveBatchList.add(groupMember);
        });
        // 更新缓存
        String key = RedisKeyConst.getKey(RedisKeyConst.GROUP_INFO_STRING, groupId);
        GroupBaseInfo groupBaseInfo = RedisUtil.get(key, GroupBaseInfo.class);
        AssertUtil.isNotEmpty(groupBaseInfo, CommonErrorEnum.SYSTEM_ERROR.getMsg());
        // 合并群成员并去重
        groupBaseInfo.getMemberList().addAll(uidList);
        Set<Long> uidSet = new HashSet<>(groupBaseInfo.getMemberList());
        groupBaseInfo.setMemberList(new ArrayList<>(uidSet));
        RedisUtil.set(key, groupBaseInfo);
        this.saveBatch(saveBatchList);
    }

    public Boolean hasPowerForInviteGroup(Long groupId, Long uid) {

        return this.lambdaQuery()
                .eq(GroupMember::getRoomId, groupId)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.MASTER.getId())
                .or()
                .eq(GroupMember::getRoomId, groupId)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.ADMIN.getId())
                .exists();
    }

    public List<Long> getAdminCount(Long groupId) {
        return this.lambdaQuery()
                .eq(GroupMember::getRoomId, groupId)
                .eq(GroupMember::getRole, GroupRoleEnum.ADMIN.getId())
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    public void setAdmin(Long groupId, List<Long> uidList) {
        Wrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>()
                .lambda()
                .eq(GroupMember::getRoomId, groupId)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.ADMIN.getId());
        this.update(wrapper);
    }

    public void delAdmin(Long groupId, List<Long> uidList) {
        Wrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>()
                .lambda()
                .eq(GroupMember::getRoomId, groupId)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MEMBER.getId());
        this.update(wrapper);
    }
}
