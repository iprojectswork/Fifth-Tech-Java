package com.fifthtech.service.auth;

import com.fifthtech.dto.auth.LoginRequestDTO;
import com.fifthtech.vo.auth.LoginResponseVO;
import com.fifthtech.vo.user.UserInfoVO;
import com.fifthtech.vo.permission.PermissionTreeVO;

import java.util.List;

/**
 * @author RH
 * @ClassName AuthService
 * @description: 认证服务接口
 * @date 2026年08月16日
 * @version: 1.0
 */
public interface AuthService {

    /**
    * @description: 用户名密码登录，校验通过则生成 Token 写入 Redis
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [request]
    * @return: {@link LoginResponseVO}
    **/
    LoginResponseVO login(LoginRequestDTO request);

    /**
    * @description: 查询当前登录用户信息与权限码
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link UserInfoVO}
    **/
    UserInfoVO getUserInfo();

    /**
    * @description: 查询当前用户菜单树，含祖先节点补齐
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link PermissionTreeVO}>
    **/
    List<PermissionTreeVO> getMenus();

    /**
    * @description: 退出登录，根据用户id清除Token
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: void
    **/
    void logout();
}
