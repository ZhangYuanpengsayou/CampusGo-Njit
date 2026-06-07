package com.zane.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveApplicationVO {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private String college;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long auditorId;
    private String auditorName;
    private String auditOpinion;
    private LocalDateTime auditTime;
    private LocalDateTime returnTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
