package com.gugugu.haochat.websocket.constant;

import io.netty.util.AttributeKey;

public class AuthorizationConst {
    /**
     * channel中上下文传递的token键名
     */
    public static final AttributeKey<String> TOKEN_KEY_IN_CHANNEL = AttributeKey.valueOf("Authorization");

    /**
     * channel上下文传递的ip键名
     */
    public static final AttributeKey<String> IP_KEY_IN_CHANNEL = AttributeKey.valueOf("ip");

    /**
     * channel上下文传递的用户ID键名
     */
    public static final AttributeKey<Long> UID_KEY_IN_CHANNEL = AttributeKey.valueOf("uid");
}
