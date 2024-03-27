package com.gugugu.haochat.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gugugu.haochat.chat.dao.MessageDAO;
import com.gugugu.haochat.chat.domain.entity.Message;
import com.gugugu.haochat.chat.domain.vo.req.message.ChatMessageReq;
import com.gugugu.haochat.chat.domain.vo.req.message.MessageExtra;
import com.gugugu.haochat.chat.domain.vo.req.message.body.ImageMessageReqBody;
import com.gugugu.haochat.chat.domain.vo.resp.message.ChatMessageResp;
import com.gugugu.haochat.chat.domain.vo.resp.message.body.ImageMessageRespBody;
import com.gugugu.haochat.common.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ImageMessageHandler extends AbstractMessageHandler<ImageMessageRespBody> {

    @Resource
    private MessageDAO messageDao;

    /**
     * 消息类型
     *
     * @return 消息类型
     */
    @Override
    MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    /**
     * 校验消息——保存前校验
     *
     * @param chatMessageReq 请求消息体
     * @param uid            发送消息的用户ID
     */
    @Override
    public void checkMessage(ChatMessageReq chatMessageReq, Long uid) {
        // TODO AI鉴黄处理
        return;
    }

    /**
     * 保存消息
     *
     * @param message        消息
     * @param chatMessageReq 请求消息体
     */
    @Override
    public void saveMessage(Message message, ChatMessageReq chatMessageReq) {
        MessageExtra messageExtra = new MessageExtra();
        Object body = chatMessageReq.getBody();
        ImageMessageReqBody imageMessageReqBody = BeanUtil.toBean(body, ImageMessageReqBody.class);
        messageExtra.setImageMessageReqBody(imageMessageReqBody);
        message.setExtra(messageExtra);
        messageDao.updateById(message);
    }

    /**
     * 构建响应消息体
     *
     * @param message 消息对象
     * @param builder 构造器
     * @return 响应消息体
     */
    @Override
    public ChatMessageResp.Message buildChatMessageResp(Message message, ChatMessageResp.Message.MessageBuilder builder) {
        ImageMessageRespBody imageMessageReqBody = this.buildResponseBody(message);
        return builder.body(imageMessageReqBody).build();
    }

    /**
     * 构建消息返回体对象
     *
     * @param message 消息体对象
     * @return 消息体对象
     */
    @Override
    public ImageMessageRespBody buildResponseBody(Message message) {
        ImageMessageRespBody imageMessageRespBody = new ImageMessageRespBody();
        ImageMessageReqBody imageMessageReqBody = message.getExtra().getImageMessageReqBody();

        if (ObjectUtil.isNotNull(imageMessageReqBody)) {
            imageMessageRespBody.setSize(imageMessageReqBody.getSize());
            imageMessageRespBody.setWidth(imageMessageReqBody.getWidth());
            imageMessageRespBody.setHeight(imageMessageReqBody.getHeight());
            imageMessageRespBody.setUrl(imageMessageReqBody.getUrl());
        }
        return imageMessageRespBody;
    }

    /**
     * 被回复时——展示的消息
     *
     * @param message 消息体
     * @return 被回复时——展示的消息
     */
    @Override
    public String showInReplyMessage(Message message) {
        return "[图片]";
    }

    /**
     * 会话列表——展示的消息
     *
     * @param message 消息体
     * @return 会话列表——展示的消息
     */
    @Override
    public String showInContactMessage(Message message) {
        return "[图片]";
    }
}
