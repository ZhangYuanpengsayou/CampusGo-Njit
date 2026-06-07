package com.zane.entity;

import lombok.Data;

@Data
public class StudentProfile {

    private Long id;
    private Long userId;
    private String studentNo;
    private String major;
    private String className;
    private String dormBuilding;
    private String dormRoom;
}
