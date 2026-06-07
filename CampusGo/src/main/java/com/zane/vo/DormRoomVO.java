package com.zane.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DormRoomVO {

    private Long id;
    private Long buildingId;
    private String buildingName;
    private String roomNo;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
