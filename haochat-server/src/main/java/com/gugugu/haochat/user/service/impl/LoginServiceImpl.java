package com.gugugu.haochat.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.gugugu.haochat.common.constant.RedisKeyConst;
import com.gugugu.haochat.common.utils.JwtUtil;
import com.gugugu.haochat.common.utils.RedisUtil;
import com.gugugu.haochat.user.service.LoginService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

import static com.gugugu.haochat.common.constant.UserConst.USER_TOKEN_SECRET;
import static com.gugugu.haochat.common.constant.UserConst.USER_TOKEN_TTL;
@Service
public class LoginServiceImpl implements LoginService {
    public static final String NULL = "null";
    @Override
    public String login(Long uid) {
        String key = RedisKeyConst.getKey(RedisKeyConst.USER_TOKEN_STRING, uid);
        //获取用户token
        String token = RedisUtil.getStr(key);
        if(StrUtil.isNotBlank(token)){
            return token;
        }
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("uid", uid);
        token = JwtUtil.create(map, USER_TOKEN_SECRET, USER_TOKEN_TTL);
        return token;
    }

    @Override
    public boolean verify(String token) {
        try {
            JwtUtil.parse(token, USER_TOKEN_SECRET);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Serializable getValidUid(String token) {
        if (StrUtil.isEmpty(token) || NULL.equals(token)) {
            return null;
        }
        Claims userInfo = JwtUtil.parse(token, USER_TOKEN_SECRET);
        return Long.parseLong(userInfo.get("uid").toString());
    }
}
