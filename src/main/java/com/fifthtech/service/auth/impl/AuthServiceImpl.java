package com.fifthtech.service.auth.impl;

import com.fifthtech.dto.auth.LoginRequestDTO;
import com.fifthtech.vo.auth.LoginResponseVO;
import com.fifthtech.vo.user.UserInfoVO;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.security.TokenUtils;
import com.fifthtech.security.UserContext;
import com.fifthtech.service.auth.AuthService;
import com.fifthtech.service.permission.PermissionService;
import com.fifthtech.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public LoginResponseVO login(LoginRequestDTO request) {
        User user = userService.selectByUsername(request.getUsername());
        if (user == null) {
            return new LoginResponseVO(false, null, "用户不存在！");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponseVO(false, null, "密码错误");
        }
        String token = tokenUtils.generateToken(user.getId());
        return new LoginResponseVO(true, token, "登录成功");
    }

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return null;
        }
        User user = userService.selectById(userId);
        if (user == null) {
            return null;
        }
        List<String> permissions = permissionService.getPermissionCodesByUserId(userId);
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        return new UserInfoVO(user.getId(), user.getUsername(), user.getNickname(), permissions);
    }

    @Override
    public List<PermissionTreeVO> getMenus() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Collections.emptyList();
        }
        List<PermissionTreeVO> menus = permissionService.getMenuTreeByUserId(userId);
        return menus != null ? menus : Collections.emptyList();
    }

    @Override
    public void logout() {
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            tokenUtils.removeTokenByUserId(userId);
        }
    }
}
