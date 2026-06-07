package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String role;
    private String realName;
    private String phone;
    private String email;
    private String college;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
