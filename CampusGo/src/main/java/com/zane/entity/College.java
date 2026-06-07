package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class College {

    private Long id;
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
