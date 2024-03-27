package com.gugugu.haochat.chat.service.strategy.msg.factory;

import com.gugugu.haochat.chat.service.strategy.msg.AbstractMessageHandler;
import com.gugugu.haochat.common.domain.enums.error.CommonErrorEnum;
import com.gugugu.haochat.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerFactory {

    /**
     * code -> handler映射关系
     */
    private static final Map<Integer, AbstractMessageHandler<Object>> STRATEGY_MAP = new HashMap<>();

    /**
     * 注册消息处理器
     *
     * @param code     注册码
     * @param strategy 消息处理器策略
     */
    public static void register(Integer code, AbstractMessageHandler<Object> strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    /**
     * 通过策略码获取相应消息策略
     *
     * @param type 策略类型
     * @return 策略
     */
    public static AbstractMessageHandler<Object> getStrategyNoNull(Integer type) {
        AbstractMessageHandler<Object> strategy = STRATEGY_MAP.get(type);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
