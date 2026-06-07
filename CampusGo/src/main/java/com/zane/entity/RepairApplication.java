package com.zane.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairApplication {

    private Long id;
    private Long studentId;
    private String reason;
    private String photoUrl;
    private String dormBuilding;
    private String dormRoom;
    private String status;
    private Long auditorId;
    private String auditOpinion;
    private LocalDateTime auditTime;
    private String repairmanPhone;
    private Integer score;
    private String comment;
    private LocalDateTime commentTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String studentName;
    private String studentNo;
    private String auditorName;
}
