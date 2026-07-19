package com.fifthtech.service.auth;

import com.fifthtech.dto.auth.LoginRequestDTO;
import com.fifthtech.vo.auth.LoginResponseVO;
import com.fifthtech.vo.user.UserInfoVO;
import com.fifthtech.vo.permission.PermissionTreeVO;

import java.util.List;

public interface AuthService {

    LoginResponseVO login(LoginRequestDTO request);

    UserInfoVO getUserInfo();

    List<PermissionTreeVO> getMenus();

    void logout();
}
