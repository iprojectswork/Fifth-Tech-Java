package com.fifthtech.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author RH
 * @ClassName UserInfoVO
 * @description: 当前登录用户视图
 * @date 2026年08月16日
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 权限编码列表
     */
    private List<String> permissions;
}