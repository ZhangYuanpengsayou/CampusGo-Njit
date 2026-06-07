package com.zane.mapper;

import com.zane.entity.LeaveApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LeaveApplicationMapper {

    LeaveApplication findById(Long id);

    List<LeaveApplication> findByStudentPage(
            @Param("studentId") Long studentId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    long countByStudent(@Param("studentId") Long studentId, @Param("status") String status);

    List<LeaveApplication> findByCollegePage(
            @Param("college") String college,
            @Param("status") String status,
            @Param("studentName") String studentName,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    long countByCollege(
            @Param("college") String college,
            @Param("status") String status,
            @Param("studentName") String studentName);

    int insert(LeaveApplication leaveApplication);

    int cancelByStudent(@Param("id") Long id, @Param("studentId") Long studentId);

    int returnByStudent(@Param("id") Long id, @Param("studentId") Long studentId);

    int auditByTeacher(
            @Param("id") Long id,
            @Param("college") String college,
            @Param("auditorId") Long auditorId,
            @Param("status") String status,
            @Param("auditOpinion") String auditOpinion);
}
