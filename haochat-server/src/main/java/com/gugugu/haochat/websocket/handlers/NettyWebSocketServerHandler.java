package com.gugugu.haochat.websocket.handlers;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.gugugu.haochat.common.domain.enums.WsReqTypeEnum;
import com.gugugu.haochat.common.domain.vo.req.WsBaseReq;
import com.gugugu.haochat.websocket.constant.AuthorizationConst;
import com.gugugu.haochat.websocket.service.WebSocketService;
import com.gugugu.haochat.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.netty.handler.codec.mqtt.MqttMessageBuilders.connAck;
import static io.netty.handler.codec.mqtt.MqttMessageBuilders.disconnect;

@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private WebSocketService webSocketService;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //握手认证
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            //保存连接
            webSocketService.connet(ctx.channel());
            //在协议升级前获取http请求头中的token
            String token = NettyUtil.getAttrFromChannel(ctx.channel(), AuthorizationConst.TOKEN_KEY_IN_CHANNEL);
            if(StrUtil.isNotBlank(token)){
                //权限验证
                webSocketService.authorize(ctx.channel(),token);
            }
            //读空闲
            if(evt instanceof IdleStateEvent){
                IdleStateEvent event = (IdleStateEvent) evt;
                if(event.state() == IdleState.READER_IDLE){
                    log.info("长时间无操作，断开连接");
                    disconnect(ctx.channel());
                }
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)  {
        this.webSocketService = SpringUtil.getBean(WebSocketService.class);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        disconnect(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        disconnect(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常捕获",cause);
        super.exceptionCaught(ctx,cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("message: {}", msg.text());
        Channel channel = ctx.channel();
        //转文本
        String text = msg.text();
        //转为websocket请求对象(包装消息)
        WsBaseReq wsBaseReq = JSONUtil.toBean(text, WsBaseReq.class);
        //获取消息类型
        WsReqTypeEnum type = WsReqTypeEnum.of(wsBaseReq.getType());
        switch (type){
            case AUTHORIZE:
                //授权
                webSocketService.authorize(channel,wsBaseReq.getData());
                break;
            case LOGIN:
                //登录
                webSocketService.scan(channel);
                break;
            case LOGOUT:
                //退出
                webSocketService.logout(channel);
                break;
            case HEARTBEAT:
                log.info("type: {}", type);
                break;
            default:
                //未知请求
                Map<Integer, WsReqTypeEnum> wsRequestTypeCache = WsReqTypeEnum.WS_REQUEST_TYPE_CACHE;
                String wsRequestTypeCacheStr = JSONUtil.toJsonStr(wsRequestTypeCache);
                channel.writeAndFlush(new TextWebSocketFrame(wsRequestTypeCacheStr));
        }
    }

    private void disconnect(Channel channel){
        webSocketService.disconnect(channel);
        channel.close();
    }
}
