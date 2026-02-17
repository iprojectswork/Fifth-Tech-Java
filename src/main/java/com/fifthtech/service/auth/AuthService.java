package com.fifthtech.service.auth;

import com.fifthtech.controller.auth.LoginRequestDTO;
import com.fifthtech.controller.auth.LoginResponseVO;

public interface AuthService {

    LoginResponseVO login(LoginRequestDTO request);
}
