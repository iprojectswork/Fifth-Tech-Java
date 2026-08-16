package com.fifthtech.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RH
 * @ClassName LoginResponseVO
 * @description: 登录响应
 * @date 2026年08月16日
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 提示信息
     */
    private String message;
}