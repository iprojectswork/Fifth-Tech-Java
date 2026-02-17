package com.fifthtech.controller.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "用户名不能为�?)
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
