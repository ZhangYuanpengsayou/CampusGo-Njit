package com.zane.dto;

import lombok.Data;

@Data
public class CarouselItemDTO {

    private String title;
    private String subtitle;
    private String imageUrl;
    private Integer sortOrder;
    private Integer status;
}
