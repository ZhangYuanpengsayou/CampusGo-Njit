package com.zane.mapper;

import com.zane.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User findById(Long id);

    User findByUsername(String username);

    int insert(User user);

    int updateProfile(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
