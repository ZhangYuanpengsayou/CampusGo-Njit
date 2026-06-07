package com.zane.service.impl;

import com.zane.common.constant.RoleConstants;
import com.zane.common.constant.StatusConstants;
import com.zane.dto.LeaveAuditDTO;
import com.zane.dto.LeaveCreateDTO;
import com.zane.entity.LeaveApplication;
import com.zane.entity.User;
import com.zane.exception.BusinessException;
import com.zane.mapper.LeaveApplicationMapper;
import com.zane.mapper.UserMapper;
import com.zane.vo.LeaveApplicationVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock
    private LeaveApplicationMapper leaveApplicationMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    @Test
    void createLeaveRejectsInvalidTimeRange() {
        LeaveCreateDTO dto = new LeaveCreateDTO();
        dto.setReason("就医");
        dto.setStartTime(LocalDateTime.of(2026, 6, 11, 8, 0));
        dto.setEndTime(LocalDateTime.of(2026, 6, 10, 18, 0));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> leaveService.createLeave(1L, dto)
        );

        assertEquals("请假开始时间不能晚于结束时间", exception.getMessage());
    }

    @Test
    void createLeaveUsesStudentCollegeAndPendingStatus() {
        LeaveCreateDTO dto = new LeaveCreateDTO();
        dto.setReason("就医");
        dto.setStartTime(LocalDateTime.of(2026, 6, 10, 8, 0));
        dto.setEndTime(LocalDateTime.of(2026, 6, 10, 18, 0));

        User student = new User();
        student.setId(1L);
        student.setRole(RoleConstants.STUDENT);
        student.setCollege("计算机学院");
        when(userMapper.findById(1L)).thenReturn(student);
        doAnswer(invocation -> {
            LeaveApplication leave = invocation.getArgument(0);
            leave.setId(20L);
            return 1;
        }).when(leaveApplicationMapper).insert(any(LeaveApplication.class));
        when(leaveApplicationMapper.findById(20L)).thenReturn(buildLeave(StatusConstants.Leave.PENDING, "计算机学院"));

        LeaveApplicationVO vo = leaveService.createLeave(1L, dto);

        assertEquals(20L, vo.getId());
        assertEquals(StatusConstants.Leave.PENDING, vo.getStatus());
        assertEquals("计算机学院", vo.getCollege());
    }

    @Test
    void auditLeaveRejectsOtherCollegeApplication() {
        User teacher = new User();
        teacher.setId(2L);
        teacher.setRole(RoleConstants.TEACHER);
        teacher.setCollege("计算机学院");
        when(userMapper.findById(2L)).thenReturn(teacher);
        when(leaveApplicationMapper.findById(20L)).thenReturn(buildLeave(StatusConstants.Leave.PENDING, "外国语学院"));

        LeaveAuditDTO dto = new LeaveAuditDTO();
        dto.setStatus(StatusConstants.Leave.APPROVED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> leaveService.auditLeave(2L, 20L, dto)
        );

        assertEquals("只能审核本学院学生的请假申请", exception.getMessage());
    }

    @Test
    void cancelLeaveOnlyAllowsPendingApplication() {
        when(leaveApplicationMapper.findById(20L)).thenReturn(buildLeave(StatusConstants.Leave.APPROVED, "计算机学院"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> leaveService.cancelLeave(1L, 20L)
        );

        assertEquals("只有待审核的请假申请可以撤销", exception.getMessage());
    }

    @Test
    void auditLeaveUpdatesPendingApplication() {
        User teacher = new User();
        teacher.setId(2L);
        teacher.setRole(RoleConstants.TEACHER);
        teacher.setCollege("计算机学院");
        when(userMapper.findById(2L)).thenReturn(teacher);
        when(leaveApplicationMapper.findById(20L))
                .thenReturn(buildLeave(StatusConstants.Leave.PENDING, "计算机学院"))
                .thenReturn(buildLeave(StatusConstants.Leave.APPROVED, "计算机学院"));

        LeaveAuditDTO dto = new LeaveAuditDTO();
        dto.setStatus(StatusConstants.Leave.APPROVED);
        dto.setAuditOpinion("同意");

        LeaveApplicationVO vo = leaveService.auditLeave(2L, 20L, dto);

        assertEquals(StatusConstants.Leave.APPROVED, vo.getStatus());
        verify(leaveApplicationMapper).auditByTeacher(20L, "计算机学院", 2L, StatusConstants.Leave.APPROVED, "同意");
    }

    private LeaveApplication buildLeave(String status, String college) {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(20L);
        leave.setStudentId(1L);
        leave.setStudentName("张三");
        leave.setCollege(college);
        leave.setReason("就医");
        leave.setStartTime(LocalDateTime.of(2026, 6, 10, 8, 0));
        leave.setEndTime(LocalDateTime.of(2026, 6, 10, 18, 0));
        leave.setStatus(status);
        return leave;
    }
}
