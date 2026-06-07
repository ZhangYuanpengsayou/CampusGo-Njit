package com.zane.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MajorVO {

    private Long id;
    private Long collegeId;
    private String collegeName;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
