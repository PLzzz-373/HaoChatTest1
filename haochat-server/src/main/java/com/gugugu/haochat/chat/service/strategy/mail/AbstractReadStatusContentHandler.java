package com.gugugu.haochat.chat.service.strategy.mail;

import com.gugugu.haochat.chat.service.strategy.mail.factory.MailContentReadStatusFactory;
import com.gugugu.haochat.user.domain.entity.UserApply;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public abstract class AbstractReadStatusContentHandler {

    @PostConstruct
    public void init() {
        MailContentReadStatusFactory.register(this.getStatus(), this);
    }

    /**
     * 获取消息处理器状态
     *
     * @return 消息处理器状态
     */
    public abstract Integer getStatus();

    /**
     * 添加朋友申请
     *
     * @param userApply 申请记录
     * @return 内容
     */
    public abstract String showContentInAddFriend(UserApply userApply);

    /**
     * 邀请加群申请
     *
     * @param userApply 申请记录
     * @return 内容
     */
    public abstract String showContentInInviteGroup(UserApply userApply);

}
