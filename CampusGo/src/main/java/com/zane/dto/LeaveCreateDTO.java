package com.zane.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveCreateDTO {

    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
