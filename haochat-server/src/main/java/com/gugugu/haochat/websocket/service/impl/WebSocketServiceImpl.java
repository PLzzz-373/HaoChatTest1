package com.gugugu.haochat.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.domain.enums.WsRespTypeEnum;
import com.gugugu.haochat.common.domain.vo.req.WsBaseReq;
import com.gugugu.haochat.common.event.UserOfflineEvent;
import com.gugugu.haochat.common.event.UserOnlineEvent;
import com.gugugu.haochat.common.event.adapter.EventParamAdapter;
import com.gugugu.haochat.common.event.domain.dto.UserOnlineEventParamsDTO;
import com.gugugu.haochat.common.utils.JsonUtil;
import com.gugugu.haochat.common.utils.RedisUtil;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.dao.UserRoleDAO;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.service.LoginService;
import com.gugugu.haochat.websocket.adapter.WsAdapter;
import com.gugugu.haochat.websocket.constant.AuthorizationConst;
import com.gugugu.haochat.websocket.domain.dto.WsConnectInfoDTO;
import com.gugugu.haochat.websocket.domain.vo.resp.WsBaseResp;
import com.gugugu.haochat.websocket.domain.vo.resp.WsLoginSuccessMessage;
import com.gugugu.haochat.websocket.service.WebSocketService;
import com.gugugu.haochat.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static com.gugugu.haochat.common.constant.UserConst.TEMP_USER_UID;
import static com.gugugu.haochat.common.domain.enums.WsRespTypeEnum.CONN_SUCCESS;

@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {
    /**
     * 存储所有websocket连接和用户信息的映射
     */
    public static final ConcurrentHashMap<Channel, WsConnectInfoDTO> CHANNEL_CONNECT_MAP = new ConcurrentHashMap<>();
    /**
     * 存储所有已经连接的在线用户ID和channel的映射
     */
    public static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> UID_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 所有登录请求的code与channel的映射关系
     *
     */
    public static final Cache<Integer,Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10000L)
            .build();

    /**
     * 登录有效期
     */
    private static final Duration LOGIN_EXPIRE_TIME = Duration.ofHours(1);

    /**
     * 设置每个账户的最大登录数
     */
    public static final Integer MAX_CONCURRENT_LONGIN = 3;

    @Resource
    private LoginService loginService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserRoleDAO userRoleDAO;
    @Resource
    private WxMpService wxMpService;
    @Override
    public void connet(Channel channel) {
        CHANNEL_CONNECT_MAP.put(channel,new WsConnectInfoDTO());
        WsBaseResp<String> wsBaseResp = new WsBaseResp<>();
        String token = loginService.login(TEMP_USER_UID);
        wsBaseResp.setType(CONN_SUCCESS.getType()).setData(token);
        sendMsgToOne(channel, wsBaseResp);
        log.info("CHANNEL_CONN_MAP: {}", CHANNEL_CONNECT_MAP);

    }

    @Override
    public void disconnect(Channel channel) {
        // 删除全部连接（包括未登录的）
        CHANNEL_CONNECT_MAP.remove(channel);
        // 删除用户连接（设置为离线）
        // 判断当前连接是否是以登录态
        Long uid = NettyUtil.getAttrFromChannel(channel, AuthorizationConst.UID_KEY_IN_CHANNEL);
        if (uid == null) {
            return;
        }
        offline(channel, uid);
    }

    @Override
    public void authorize(Channel channel, String token) {
        boolean verifySuccess = loginService.verify(token);
        if(verifySuccess){
            User user = userDAO.getById(loginService.getValidUid(token));
            loginSuccess(channel,user,token);
        }
    }


    private void loginSuccess(Channel channel, User user, String token) {
        online(channel, user.getId());
        // 构建参数
        // 查询权限
        Long power = userRoleDAO.getPower(user);
        WsBaseResp<WsLoginSuccessMessage> wsBaseResp = WsAdapter.buildLoginSuccessResp(user, token, power);
        UserOnlineEventParamsDTO userOnlineEventParamsDTO = EventParamAdapter.buildUserOnlineEventParams(channel, wsBaseResp);
        // 发送用户上线事件
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, userOnlineEventParamsDTO));
    }

    private void online(Channel channel, Long uid) {
        // 记录当前连接信息
        UID_CHANNEL_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        // 这里必须使用add，不然不支持单账户多端登录
        // 这里可以限制登录设备台数
        CopyOnWriteArrayList<Channel> channels = UID_CHANNEL_MAP.get(uid);
        if (TEMP_USER_UID.equals(uid) || channels.size() < MAX_CONCURRENT_LONGIN) {
            channels.add(channel);
        } else {
            WsBaseResp<Void> wsBaseResp = new WsBaseResp<>();
            wsBaseResp.setType(WsRespTypeEnum.LIMIT_CONCURRENT_LOGIN.getType());
            sendMsgToOne(channel, wsBaseResp);
        }
        // 将用户id记录在当前通道中
        NettyUtil.setAttrInChannel(channel, AuthorizationConst.UID_KEY_IN_CHANNEL, uid);
    }

    private void offline(Channel channel, Long uid) {
        UID_CHANNEL_MAP.remove(uid);
        applicationEventPublisher.publishEvent(new UserOfflineEvent(this, uid));
    }
    @Override
    public void sendMsgToOne(Channel channel, WsBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    @SneakyThrows
    @Override
    public void scan(Channel channel) {
        //生成随机不重复登录码，并且保存与channel映射关系在本地缓存中
        Integer code = generateLoginCode(channel);
        //获取登录二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) LOGIN_EXPIRE_TIME.getSeconds());
        sendMsgToOne(channel,WsAdapter.buildLoginResp(wxMpQrCodeTicket));

    }

    @Override
    public void logout(Channel channel) {
        Long uid = NettyUtil.getAttrFromChannel(channel, AuthorizationConst.UID_KEY_IN_CHANNEL);
        // 用户下线
        offline(channel, uid);
    }

    @Override
    public void scanLoginSuccess(Integer code, User user, String token) {
        // 发送消息
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        // 判断等待列表中是否存在该通道
        if (Objects.isNull(channel)) {
            return;
        }
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        // 用户登录
        loginSuccess(channel, user, token);
    }

    @Override
    public void subscribeSuccess(Integer code) {
        if (code == null) {
            // 这个地方是防止意外情况的发生：
            //  例如：当用户不是从扫码登录过来的，而是直接通过微信搜索进行了订阅公众号，从而导致报错
            return;
        }
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            // 超时或已移除 code -> channel ×
            // TODO 通知用户二维码已过期，刷新二维码
            return;
        }
        Long uid = NettyUtil.getAttrFromChannel(channel, AuthorizationConst.UID_KEY_IN_CHANNEL);
        String ip = NettyUtil.getAttrFromChannel(channel, AuthorizationConst.IP_KEY_IN_CHANNEL);
        User user = userDAO.getById(uid);
        User update = new User();
        update.setId(user.getId());
        user.refreshIp(ip);
        update.setIpInfo(user.getIpInfo());
        update.setLastOptTime(new Date());
        sendMsgToOne(channel, WsAdapter.buildSubscribeSuccessResp());
    }


    private Integer generateLoginCode(Channel channel) {
        int code;
        do{
            code = RedisUtil.integerInc(RedisKeyConst.getKey(RedisKeyConst.LOGIN_CODE),(int) LOGIN_EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);

        }while (WAIT_LOGIN_MAP.asMap().containsKey(code));
        WAIT_LOGIN_MAP.put(code,channel);
        return code;
    }
}
