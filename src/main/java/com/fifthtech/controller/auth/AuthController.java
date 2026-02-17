package com.fifthtech.controller.auth;

import com.fifthtech.common.Result;
import com.fifthtech.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController
 *
 * @author RH
 * @description 认证控制�? * @date 2026-01-25
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseVO response = authService.login(request);
        if (response.getSuccess()) {
            return Result.success(response.getMessage(), response);
        } else {
            return Result.error(response.getMessage());
        }
    }
}
