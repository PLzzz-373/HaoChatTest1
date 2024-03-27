package com.gugugu.haochat.chat.dao;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.RoomFriend;
import com.gugugu.haochat.chat.mapper.RoomFriendMapper;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.enums.FriendStatusEnum;
import com.gugugu.haochat.common.utils.RedisUtil;
import com.gugugu.haochat.user.domain.vo.resp.UserSearchRespVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomFriendDAO extends ServiceImpl<RoomFriendMapper, RoomFriend> {
    @Resource
    private RoomDAO roomDAO;
    public RoomFriend getUidByRoomId(Long roomId) {

        return lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .select(RoomFriend::getUid1, RoomFriend::getUid2)
                .one();
    }

    public Boolean isFriend(Long roomId, Long uid) {
        RoomFriend one = lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .eq(RoomFriend::getUid1, uid)
                .or()
                .eq(RoomFriend::getRoomId, roomId)
                .eq(RoomFriend::getUid2, uid)
                .one();
        return ObjUtil.isNotNull(one);

    }

    public  Boolean isFriend(List<Long> uids) {
        String roomKey;
        Long uid1 = uids.get(0);
        Long uid2 = uids.get(1);
        if(uid1 > uid2){
            roomKey = uid2 + "_" + uid1;
        }else {
            roomKey = uid1 + "_" + uid2;
        }
        return ObjUtil.isNotNull(lambdaQuery().eq(RoomFriend::getRoomKey,roomKey).one());
    }

    public List<UserSearchRespVO> isFriend(Long uid, List<Long> uidList) {
        List<UserSearchRespVO> list = new ArrayList<>();
        Boolean isLogin = ObjectUtil.isNotNull(uid);
        for (Long id : uidList) {
            UserSearchRespVO userSearchRespVO = new UserSearchRespVO();
            if (isLogin) {
                Boolean isFriend = this.isFriend(Arrays.asList(uid, id));
                userSearchRespVO.setUid(id);
                userSearchRespVO.setIsFriend(isFriend);
            } else {
                userSearchRespVO.setIsFriend(false);
            }
            list.add(userSearchRespVO);
        }
        return list;
    }

    public RoomFriend getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .one();
    }

    public List<Long> listFriendIdByUid(Long uid) {
        return this.lambdaQuery()
                .eq(RoomFriend::getUid1, uid)
                .or()
                .eq(RoomFriend::getUid2, uid).list()
                .stream()
                .map(roomFriend -> {
                    if (roomFriend.getUid1().equals(uid)) {
                        return roomFriend.getUid2();
                    } else {
                        return roomFriend.getUid1();
                    }
                }).collect(Collectors.toList());
    }

    public Long getRoomIdByUid(Long uid1, Long uid2) {
        String roomKey = this.getRoomKey(uid1, uid2);
        RoomFriend roomFriend = this.lambdaQuery()
                .eq(RoomFriend::getRoomKey, roomKey)
                .one();
        return roomFriend.getRoomId();
    }

    private String getRoomKey(Long uid1, Long uid2) {
        String roomKey;
        if (uid1 < uid2) {
            roomKey = uid1 + "_" + uid2;
        } else {
            roomKey = uid2 + "_" + uid1;
        }
        return roomKey;
    }

    public Long delFriend(Long uid, Long friendId) {
        String roomFriendKey = this.getRoomKey(uid, friendId);
        LambdaQueryWrapper<RoomFriend> wrapper = new QueryWrapper<RoomFriend>().lambda()
                .eq(RoomFriend::getRoomKey, roomFriendKey);
        // 删除房间
        RoomFriend roomFriend = this.getOne(wrapper);
        Long roomId = roomFriend.getRoomId();
        roomDAO.deleteById(roomId);
        this.remove(wrapper);

        // 同步删除缓存
        String friendKey = String.format(RedisKeyConst.FRIEND_INFO_STRING, roomId);
        String roomKey = String.format(RedisKeyConst.ROOM_INFO_STRING, roomId);
        RedisUtil.del(friendKey, roomKey);

        return roomId;
    }

    public void addFriend(Long roomId, Long uid, Long targetId) {
        String roomKey = this.getRoomKey(uid, targetId);
        Long uid1;
        Long uid2;
        if (uid < targetId) {
            uid1 = uid;
            uid2 = targetId;
        } else {
            uid1 = targetId;
            uid2 = uid;
        }
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(uid1);
        roomFriend.setUid2(uid2);
        roomFriend.setRoomKey(roomKey);
        roomFriend.setStatus(FriendStatusEnum.NORMAL.getCode());
        this.save(roomFriend);
    }
}
