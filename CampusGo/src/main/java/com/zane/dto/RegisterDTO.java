package com.zane.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;
    private String password;
    private String confirmPassword;
    private String role;
    private String realName;
    private String phone;
    private String email;
    private String college;
    private String applicationCode;

    private String studentNo;
    private String major;
    private String className;
    private String dormBuilding;
    private String dormRoom;

    private String teacherNo;
    private String title;
    private String office;

    private String adminNo;
    private String department;
}
