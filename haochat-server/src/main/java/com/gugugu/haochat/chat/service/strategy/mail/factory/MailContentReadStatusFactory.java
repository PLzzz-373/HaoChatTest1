package com.gugugu.haochat.chat.service.strategy.mail.factory;

import com.gugugu.haochat.chat.service.strategy.mail.AbstractReadStatusContentHandler;

import java.util.HashMap;
import java.util.Map;

public class MailContentReadStatusFactory {

    public static final Map<Integer, AbstractReadStatusContentHandler> STATUS_FACTORY = new HashMap<>(8);

    /**
     * 注册处理器
     *
     * @param status  状态
     * @param handler 处理器
     */
    public static void register(Integer status, AbstractReadStatusContentHandler handler) {
        STATUS_FACTORY.put(status, handler);
    }

    /**
     * 获取处理器
     *
     * @param status 状态
     * @return 处理器
     */
    public static AbstractReadStatusContentHandler getHandler(int status) {
        return STATUS_FACTORY.get(status);
    }

}
