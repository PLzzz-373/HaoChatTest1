package com.gugugu.haochat.websocket.handlers;

import com.gugugu.haochat.common.domain.dto.RequestHolderDTO;
import com.gugugu.haochat.common.domain.dto.RequestInfo;
import com.gugugu.haochat.websocket.constant.AuthorizationConst;
import com.gugugu.haochat.websocket.utils.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyHttpParamsCollectorHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Long uid = NettyUtil.getAttrFromChannel(ctx.channel(), AuthorizationConst.UID_KEY_IN_CHANNEL);
        String ip = NettyUtil.getAttrFromChannel(ctx.channel(), AuthorizationConst.IP_KEY_IN_CHANNEL);
        RequestInfo info = new RequestInfo();
        info.setUid(uid);
        info.setIp(ip);
        RequestHolderDTO.set(info);
        ctx.fireChannelRead(msg);
    }
}
