package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DormRoom {

    private Long id;
    private Long buildingId;
    private String buildingName;
    private String roomNo;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
