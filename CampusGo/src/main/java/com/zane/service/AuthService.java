package com.zane.service;

import com.zane.common.session.LoginUser;
import com.zane.dto.LoginDTO;
import com.zane.dto.RegisterDTO;

public interface AuthService {

    void register(RegisterDTO registerDTO);

    LoginUser login(LoginDTO loginDTO);
}
