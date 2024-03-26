package com.gugugu.haochat.user.service.impl;

import com.gugugu.haochat.common.event.UserRegisterEvent;
import com.gugugu.haochat.user.dao.UserDAO;
import com.gugugu.haochat.user.domain.entity.User;
import com.gugugu.haochat.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDAO userDAO;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void register(String openId) {
        User user = User.builder().openId(openId).build();
        userDAO.save(user);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,user));

    }
}
