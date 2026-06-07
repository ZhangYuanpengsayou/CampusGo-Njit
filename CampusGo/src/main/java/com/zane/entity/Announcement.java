package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {

    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String publisherName;
}
