package com.zane.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String realName;
    private String phone;
    private String email;
    private String college;

    private String major;
    private String className;
    private String dormBuilding;
    private String dormRoom;

    private String title;
    private String office;

    private String department;
}
