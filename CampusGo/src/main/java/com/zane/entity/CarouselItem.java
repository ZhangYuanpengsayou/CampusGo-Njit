package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarouselItem {

    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
