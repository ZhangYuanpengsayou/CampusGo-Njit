package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Major {

    private Long id;
    private Long collegeId;
    private String collegeName;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
