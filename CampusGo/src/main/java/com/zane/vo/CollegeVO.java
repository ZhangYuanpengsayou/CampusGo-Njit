package com.zane.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollegeVO {

    private Long id;
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
