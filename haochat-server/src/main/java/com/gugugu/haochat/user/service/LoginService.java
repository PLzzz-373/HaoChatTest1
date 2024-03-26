package com.gugugu.haochat.user.service;

import java.io.Serializable;

public interface LoginService {
    String login(Long uid);

    boolean verify(String token);

    Serializable getValidUid(String token);
}
