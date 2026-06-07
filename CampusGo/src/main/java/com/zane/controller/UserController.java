package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import com.zane.dto.PasswordUpdateDTO;
import com.zane.dto.UserUpdateDTO;
import com.zane.exception.BusinessException;
import com.zane.service.UserService;
import com.zane.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success(userService.getCurrentUser(loginUser.getId()));
    }

    @PutMapping("/me")
    public Result<UserVO> updateCurrentUser(@RequestBody UserUpdateDTO updateDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        UserVO userVO = userService.updateCurrentUser(loginUser.getId(), updateDTO);
        session.setAttribute(
                SessionConstants.CURRENT_USER,
                new LoginUser(userVO.getId(), userVO.getUsername(), userVO.getRole(), userVO.getRealName(), userVO.getCollege())
        );
        return Result.success("修改成功", userVO);
    }

    @PutMapping("/me/password")
    public Result<Void> updatePassword(@RequestBody PasswordUpdateDTO updateDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        userService.updatePassword(loginUser.getId(), updateDTO);
        return Result.success("密码修改成功", null);
    }

    private LoginUser getLoginUser(HttpSession session) {
        LoginUser loginUser = (LoginUser) session.getAttribute(SessionConstants.CURRENT_USER);
        if (loginUser == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser;
    }
}
