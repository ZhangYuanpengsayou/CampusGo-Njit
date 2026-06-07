package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveApplication {

    private Long id;
    private Long studentId;
    private String college;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long auditorId;
    private String auditOpinion;
    private LocalDateTime auditTime;
    private LocalDateTime returnTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String studentName;
    private String studentNo;
    private String auditorName;
}
