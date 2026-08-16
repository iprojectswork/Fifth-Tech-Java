package com.fifthtech.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author RH
 * @ClassName LoginRequestDTO
 * @description: 登录请求
 * @date 2026年08月16日
 * @version: 1.0
 */
@Data
public class LoginRequestDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空！")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}