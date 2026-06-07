package com.zane.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DormBuildingVO {

    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
