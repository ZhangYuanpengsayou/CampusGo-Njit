package com.zane.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String role;
    private String realName;
    private String phone;
    private String email;
    private String college;

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
