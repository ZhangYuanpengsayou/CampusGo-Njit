package com.zane.service;

import com.zane.dto.PasswordUpdateDTO;
import com.zane.dto.UserUpdateDTO;
import com.zane.vo.UserVO;

public interface UserService {

    UserVO getCurrentUser(Long userId);

    UserVO updateCurrentUser(Long userId, UserUpdateDTO updateDTO);

    void updatePassword(Long userId, PasswordUpdateDTO updateDTO);
}
