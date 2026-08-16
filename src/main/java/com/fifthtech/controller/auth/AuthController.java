package com.fifthtech.controller.auth;

import com.fifthtech.common.Result;
import com.fifthtech.dto.auth.LoginRequestDTO;
import com.fifthtech.vo.auth.LoginResponseVO;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.user.UserInfoVO;
import com.fifthtech.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author RH
 * @ClassName AuthController
 * @description: 认证控制器
 * @date 2026年08月16日
 * @version: 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
    * @description: 登录
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [request]
    * @return: {@link Result}<{@link LoginResponseVO}>
    **/
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseVO response = authService.login(request);
        if (Boolean.TRUE.equals(response.getSuccess())) {
            return Result.success(response.getMessage(), response);
        }
        return Result.error(response.getMessage());
    }

    /**
    * @description: 查询当前登录用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link UserInfoVO}>
    **/
    @GetMapping("/user-info")
    public Result<UserInfoVO> userInfo() {
        UserInfoVO userInfo = authService.getUserInfo();
        if (userInfo == null) {
            return Result.error(401, "未登录或用户不存在");
        }
        return Result.success(userInfo);
    }

    /**
    * @description: 查询当前用户菜单
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link PermissionTreeVO}>>
    **/
    @GetMapping("/menus")
    public Result<List<PermissionTreeVO>> menus() {
        return Result.success(authService.getMenus());
    }

    /**
    * @description: 退出登录
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link Void}>
    **/
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success(null);
    }
}