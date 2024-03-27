package com.gugugu.haochat.chat.dao;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageCursorReq;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.mapper.MessageMapper;
import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.constant.MessageConst;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.domain.enums.MessageStatusEnum;
import com.gugugu.haochat.common.domain.enums.MessageTypeEnum;
import com.gugugu.haochat.common.domain.enums.RoleEnum;
import com.gugugu.haochat.common.domain.vo.resp.CursorPageBaseResp;
import com.gugugu.haochat.common.utils.CursorUtil;
import com.gugugu.haochat.user.domain.vo.resp.UserInfoCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class MessageDAO extends ServiceImpl<MessageMapper, Message> {
    public static final String FIRST_MESSAGE_FOR_CREATE_GROUP = "欢迎来到%s，开始愉快的聊天吧~";
    public static final String FIRST_MESSAGE_FOR_ADD_FRIEND = "%s,%s你们已经是好友了，开始聊天吧~";
    @Resource
    private UserCache userCache;
    public Message saveByUidAndChatMessageReq(Long uid, ChatMessageReq req) {
        Message build = Message.builder()
                .fromUid(uid)
                .roomId(req.getRoomId())
                .type(req.getMessageType())
                .replyMessageId(req.getReplyMessageId())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();
        this.save(build);
        return this.getById(build.getId());
    }

    public void saveGapCount(Long roomId, Long fromId, Long toId) {
        Long count = lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, fromId)
                .le(Message::getId, toId)
                .count();
        lambdaUpdate()
                .eq(Message::getId,toId)
                .set(Message::getGapCount,count)
                .update();
    }

    public CursorPageBaseResp<Message, String> getCursorPage(ChatMessageCursorReq req) {
        return CursorUtil.getCursorPageByMysql(this,req,wrapper->{
            wrapper.eq(Message::getStatus,MessageStatusEnum.NORMAL.getStatus())
                    .eq(Message::getRoomId,req.getRoomId());
        },Message::getCreateTime);
    }

    public Boolean hasPower(UserBaseInfo baseInfo, Long id) {
        Message message = this.lambdaQuery()
                .eq(Message::getId, id)
                .one();
        if (baseInfo.getId().equals(message.getFromUid())) {
            return true;
        }
        return ObjectUtil.isNotNull(RoleEnum.of(baseInfo.getRoleId()));
    }

    public Message revoke(UserBaseInfo baseInfo, Long id) {
        Message message = new Message();
        message.setId(id);
        message.setFromUid(baseInfo.getId());
        message.setContent(String.format(MessageConst.REVOKE_TEXT, baseInfo.getName()));
        message.setReplyMessageId(null);
        message.setGapCount(null);
        message.setType(MessageTypeEnum.REVOKE.getType());
        message.setExtra(null);
        message.setUpdateTime(new Date());

        this.updateById(message);

        return this.lambdaQuery().eq(Message::getId, id).one();
    }

    public Integer getUnReadCountByReadTime(Long roomId, Date readTime) {
        return Math.toIntExact(lambdaQuery().eq(Message::getRoomId, roomId).gt(ObjectUtil.isNotNull(readTime), Message::getCreateTime, readTime).count());
    }

    public void deleteByRoomId(Long roomId) {
        LambdaQueryWrapper<Message> wrapper = new QueryWrapper<Message>()
                .lambda()
                .eq(Message::getRoomId, roomId);
        this.remove(wrapper);
    }

    public Message createFristCreateGroupMessage(Long uid, Long roomId, String groupName) {
        Message message = new Message();
        message.setRoomId(roomId);
        message.setFromUid(uid);
        message.setContent(String.format(FIRST_MESSAGE_FOR_CREATE_GROUP, groupName));
        message.setStatus(MessageStatusEnum.NORMAL.getStatus());
        message.setType(MessageTypeEnum.TEXT.getType());
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        this.save(message);
        return message;
    }

    public Message createFristAddFriendMessage(Long roomId, Long uid, Long targetId) {
        Message message = new Message();
        message.setRoomId(roomId);
        message.setFromUid(targetId);
        List<UserInfoCache> userList = userCache.getBatchByUidList(Arrays.asList(uid, targetId));
        message.setContent(String.format(FIRST_MESSAGE_FOR_ADD_FRIEND, userList.get(0).getName(), userList.get(1).getName()));
        message.setStatus(MessageStatusEnum.NORMAL.getStatus());
        message.setType(MessageTypeEnum.TEXT.getType());
        this.save(message);
        return message;
    }
}
