package com.zane.entity;

import lombok.Data;

@Data
public class AdminProfile {

    private Long id;
    private Long userId;
    private String adminNo;
    private String department;
}
