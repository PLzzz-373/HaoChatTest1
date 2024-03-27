package com.gugugu.haochat.chat.service.strategy.mail;

import com.gugugu.haochat.common.cache.UserCache;
import com.gugugu.haochat.common.domain.dto.UserBaseInfo;
import com.gugugu.haochat.common.domain.enums.ReadStatusEnum;
import com.gugugu.haochat.user.domain.entity.UserApply;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UnreadContentHandler extends AbstractReadStatusContentHandler {

    @Resource
    private UserCache userCache;

    /**
     * 获取消息处理器状态
     *
     * @return 消息处理器状态
     */
    @Override
    public Integer getStatus() {
        return ReadStatusEnum.UNREAD.getStatus();
    }

    /**
     * 添加朋友申请
     *
     * @param userApply 申请记录
     * @return 内容
     */
    @Override
    public String showContentInAddFriend(UserApply userApply) {
        Long uid = userApply.getUid();
        UserBaseInfo baseInfo = userCache.getBaseUserInfoByUid(uid);
        return String.format("%s申请加你为好友", baseInfo.getName());
    }

    /**
     * 邀请加群申请
     *
     * @param userApply 申请记录
     * @return 内容
     */
    @Override
    public String showContentInInviteGroup(UserApply userApply) {
        Long uid = userApply.getUid();
        UserBaseInfo baseInfo = userCache.getBaseUserInfoByUid(uid);
        return String.format("%s邀请你加入群聊", baseInfo.getName());
    }
}
