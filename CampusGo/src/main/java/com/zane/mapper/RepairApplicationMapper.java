package com.zane.mapper;

import com.zane.entity.RepairApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RepairApplicationMapper {

    RepairApplication findById(Long id);

    List<RepairApplication> findByStudentPage(
            @Param("studentId") Long studentId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    long countByStudent(@Param("studentId") Long studentId, @Param("status") String status);

    List<RepairApplication> findAuditPage(
            @Param("status") String status,
            @Param("studentName") String studentName,
            @Param("dormBuilding") String dormBuilding,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    long countAudit(
            @Param("status") String status,
            @Param("studentName") String studentName,
            @Param("dormBuilding") String dormBuilding);

    int insert(RepairApplication repairApplication);

    int cancelByStudent(@Param("id") Long id, @Param("studentId") Long studentId);

    int auditByAdmin(
            @Param("id") Long id,
            @Param("auditorId") Long auditorId,
            @Param("status") String status,
            @Param("repairmanPhone") String repairmanPhone,
            @Param("auditOpinion") String auditOpinion);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int completeByStudent(@Param("id") Long id, @Param("studentId") Long studentId);

    int rateByStudent(
            @Param("id") Long id,
            @Param("studentId") Long studentId,
            @Param("score") Integer score,
            @Param("comment") String comment);
}
