package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassGroup {

    private Long id;
    private Long collegeId;
    private String collegeName;
    private Long majorId;
    private String majorName;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
