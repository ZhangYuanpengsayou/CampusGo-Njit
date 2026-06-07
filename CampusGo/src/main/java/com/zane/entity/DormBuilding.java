package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DormBuilding {

    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
