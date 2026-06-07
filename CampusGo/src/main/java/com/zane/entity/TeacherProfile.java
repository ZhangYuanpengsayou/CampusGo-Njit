package com.zane.entity;

import lombok.Data;

@Data
public class TeacherProfile {

    private Long id;
    private Long userId;
    private String teacherNo;
    private String title;
    private String office;
}
