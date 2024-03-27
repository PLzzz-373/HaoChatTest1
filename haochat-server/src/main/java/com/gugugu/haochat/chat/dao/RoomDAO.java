package com.gugugu.haochat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.Room;
import com.gugugu.haochat.chat.mapper.RoomMapper;
import com.gugugu.haochat.common.domain.enums.HotFlagEnum;
import com.gugugu.haochat.common.domain.enums.RoomTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class RoomDAO extends ServiceImpl<RoomMapper, Room> {
    public void updateRoomNewestMsg(Long roomId, Date activeTime, Long lastMsgId) {
        lambdaUpdate()
                .eq(Room::getId,roomId)
                .set(Room::getActiveTime,activeTime)
                .set(Room::getLastMsgId,lastMsgId)
                .update();
    }

    public void deleteById(Long id) {
        LambdaQueryWrapper<Room> wrapper = new QueryWrapper<Room>().lambda()
                .eq(Room::getId, id);
        this.remove(wrapper);
    }

    public Room createRoom(RoomTypeEnum roomTypeEnum) {
        Room room = new Room();
        room.setType(roomTypeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        this.save(room);
        return room;
    }
}
