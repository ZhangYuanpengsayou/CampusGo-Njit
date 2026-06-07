package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import com.zane.dto.LoginDTO;
import com.zane.dto.RegisterDTO;
import com.zane.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginUser> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        LoginUser loginUser = authService.login(loginDTO);
        session.setAttribute(SessionConstants.CURRENT_USER, loginUser);
        return Result.success("登录成功", loginUser);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功", null);
    }
}
