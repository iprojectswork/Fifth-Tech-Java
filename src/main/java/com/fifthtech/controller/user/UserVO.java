package com.fifthtech.controller.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserVO
 *
 * @author RH
 * @description 用户视图对象
 * @date 2026-01-25
 * @version 1.0
 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
