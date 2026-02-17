package com.fifthtech.service.auth.impl;

import com.fifthtech.controller.auth.LoginRequestDTO;
import com.fifthtech.controller.auth.LoginResponseVO;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.service.auth.AuthService;
import com.fifthtech.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 *
 * @author RH
 * @description 认证服务实现�?
 * @date 2026-01-25
 * @version 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Override
    public LoginResponseVO login(LoginRequestDTO request) {
        User user = userService.selectByUsername(request.getUsername());
        if (user == null) {
            return new LoginResponseVO(false, null, "用户不存�?);
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponseVO(false, null, "密码错误");
        }
        return new LoginResponseVO(true, "mock-jwt-token-" + System.currentTimeMillis(), "登录成功");
    }
}
