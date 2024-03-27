package com.gugugu.haochat.common.service;

import com.gugugu.haochat.common.constant.MqConstant;
import com.gugugu.haochat.common.domain.dto.PushMessageDTO;
import com.gugugu.haochat.common.utils.MqProducer;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PushService {
    @Resource
    private MqProducer mqProducer;

    public void sendPushMsg(WsBaseResp<?> msg, List<Long> uidList) {
        uidList.parallelStream().forEach(uid -> {
            mqProducer.sendMessage(MqConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
        });
    }

    public void sendPushMsg(WsBaseResp<?> msg, Long uid) {
        mqProducer.sendMessage(MqConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
    }

    public void sendPushMsg(WsBaseResp<?> msg) {
        mqProducer.sendMessage(MqConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }
}
