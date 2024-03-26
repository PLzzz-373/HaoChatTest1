package com.gugugu.haochat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDAO extends ServiceImpl<UserMapper, User> {
    public User getByOpenId(String openid) {
        return lambdaQuery()
                .eq(User::getOpenId, openid)
                .one();
    }
}
