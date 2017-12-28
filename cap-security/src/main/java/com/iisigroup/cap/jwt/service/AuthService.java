package com.iisigroup.cap.jwt.service;

import com.iisigroup.cap.jwt.JwtUser;

public interface AuthService {
    JwtUser register(JwtUser userToAdd);

    String login(String username, String password);

    String refresh(String oldToken);
}
