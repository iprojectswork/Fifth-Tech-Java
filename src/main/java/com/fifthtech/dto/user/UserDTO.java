package com.fifthtech.dto.user;

import lombok.Data;

/**
 * UserDTO
 *
 * @author RH
 * @description 用户数据传输对象
 * @date 2026-01-25
 * @version 1.0
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;
}
