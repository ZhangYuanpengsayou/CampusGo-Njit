package com.zane.service.impl;

import com.zane.common.constant.RoleConstants;
import com.zane.common.constant.StatusConstants;
import com.zane.dto.RepairAuditDTO;
import com.zane.dto.RepairCreateDTO;
import com.zane.dto.RepairRateDTO;
import com.zane.dto.RepairStatusDTO;
import com.zane.entity.RepairApplication;
import com.zane.entity.StudentProfile;
import com.zane.entity.User;
import com.zane.exception.BusinessException;
import com.zane.mapper.RepairApplicationMapper;
import com.zane.mapper.StudentProfileMapper;
import com.zane.mapper.UserMapper;
import com.zane.vo.RepairApplicationVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairServiceImplTest {

    @Mock
    private RepairApplicationMapper repairApplicationMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StudentProfileMapper studentProfileMapper;

    @InjectMocks
    private RepairServiceImpl repairService;

    @Test
    void createRepairUsesStudentDormAndPendingStatus() {
        RepairCreateDTO dto = new RepairCreateDTO();
        dto.setReason("水龙头漏水");
        dto.setPhotoUrl("/uploads/repair/photo.jpg");

        User student = new User();
        student.setId(1L);
        student.setRole(RoleConstants.STUDENT);
        when(userMapper.findById(1L)).thenReturn(student);

        StudentProfile profile = new StudentProfile();
        profile.setDormBuilding("3栋");
        profile.setDormRoom("502");
        when(studentProfileMapper.findByUserId(1L)).thenReturn(profile);

        doAnswer(invocation -> {
            RepairApplication repair = invocation.getArgument(0);
            repair.setId(30L);
            return 1;
        }).when(repairApplicationMapper).insert(any(RepairApplication.class));
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.PENDING, 1L));

        RepairApplicationVO vo = repairService.createRepair(1L, dto);

        assertEquals(30L, vo.getId());
        assertEquals(StatusConstants.Repair.PENDING, vo.getStatus());
        assertEquals("3栋", vo.getDormBuilding());
        assertEquals("502", vo.getDormRoom());
    }

    @Test
    void auditRepairRequiresPhoneWhenApproved() {
        User admin = new User();
        admin.setId(2L);
        admin.setRole(RoleConstants.ADMIN);
        when(userMapper.findById(2L)).thenReturn(admin);
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.PENDING, 1L));

        RepairAuditDTO dto = new RepairAuditDTO();
        dto.setStatus(StatusConstants.Repair.APPROVED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.auditRepair(2L, 30L, dto)
        );

        assertEquals("审核通过时必须填写正确的维修工手机号", exception.getMessage());
    }

    @Test
    void auditRepairUpdatesPendingApplication() {
        User admin = new User();
        admin.setId(2L);
        admin.setRole(RoleConstants.ADMIN);
        when(userMapper.findById(2L)).thenReturn(admin);
        when(repairApplicationMapper.findById(30L))
                .thenReturn(buildRepair(StatusConstants.Repair.PENDING, 1L))
                .thenReturn(buildRepair(StatusConstants.Repair.APPROVED, 1L));

        RepairAuditDTO dto = new RepairAuditDTO();
        dto.setStatus(StatusConstants.Repair.APPROVED);
        dto.setRepairmanPhone("13800000000");
        dto.setAuditOpinion("已安排维修");

        RepairApplicationVO vo = repairService.auditRepair(2L, 30L, dto);

        assertEquals(StatusConstants.Repair.APPROVED, vo.getStatus());
        verify(repairApplicationMapper).auditByAdmin(30L, 2L, StatusConstants.Repair.APPROVED, "13800000000", "已安排维修");
    }

    @Test
    void cancelRepairOnlyAllowsPendingApplication() {
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.APPROVED, 1L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.cancelRepair(1L, 30L)
        );

        assertEquals("只有待审核的报修申请可以撤销", exception.getMessage());
    }

    @Test
    void updateRepairStatusRequiresApprovedBeforeRepairing() {
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.PENDING, 1L));

        RepairStatusDTO dto = new RepairStatusDTO();
        dto.setStatus(StatusConstants.Repair.REPAIRING);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.updateRepairStatus(30L, dto)
        );

        assertEquals("只有审核通过的报修可以进入维修中", exception.getMessage());
    }

    @Test
    void updateRepairStatusDoesNotAllowAdminToComplete() {
        RepairStatusDTO dto = new RepairStatusDTO();
        dto.setStatus(StatusConstants.Repair.COMPLETED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.updateRepairStatus(30L, dto)
        );

        assertEquals("管理员只能将报修状态更新为维修中", exception.getMessage());
    }

    @Test
    void completeRepairUpdatesApprovedApplication() {
        when(repairApplicationMapper.findById(30L))
                .thenReturn(buildRepair(StatusConstants.Repair.APPROVED, 1L))
                .thenReturn(buildRepair(StatusConstants.Repair.COMPLETED, 1L));

        RepairApplicationVO vo = repairService.completeRepair(1L, 30L);

        assertEquals(StatusConstants.Repair.COMPLETED, vo.getStatus());
        verify(repairApplicationMapper).completeByStudent(30L, 1L);
    }

    @Test
    void completeRepairRequiresOwner() {
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.APPROVED, 1L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.completeRepair(9L, 30L)
        );

        assertEquals("只能确认完成自己的报修申请", exception.getMessage());
    }

    @Test
    void completeRepairRequiresApprovedOrRepairingStatus() {
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.PENDING, 1L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.completeRepair(1L, 30L)
        );

        assertEquals("只有已通过或维修中的报修申请可以确认完成", exception.getMessage());
    }

    @Test
    void rateRepairRequiresOwnerAndCompletedStatus() {
        when(repairApplicationMapper.findById(30L)).thenReturn(buildRepair(StatusConstants.Repair.COMPLETED, 1L));

        RepairRateDTO dto = new RepairRateDTO();
        dto.setScore(5);
        dto.setComment("维修及时");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> repairService.rateRepair(9L, 30L, dto)
        );

        assertEquals("只能评价自己的报修申请", exception.getMessage());
    }

    @Test
    void rateRepairUpdatesCompletedApplication() {
        when(repairApplicationMapper.findById(30L))
                .thenReturn(buildRepair(StatusConstants.Repair.COMPLETED, 1L))
                .thenReturn(buildRepair(StatusConstants.Repair.RATED, 1L));

        RepairRateDTO dto = new RepairRateDTO();
        dto.setScore(5);
        dto.setComment("维修及时");

        RepairApplicationVO vo = repairService.rateRepair(1L, 30L, dto);

        assertEquals(StatusConstants.Repair.RATED, vo.getStatus());
        verify(repairApplicationMapper).rateByStudent(30L, 1L, 5, "维修及时");
    }

    private RepairApplication buildRepair(String status, Long studentId) {
        RepairApplication repair = new RepairApplication();
        repair.setId(30L);
        repair.setStudentId(studentId);
        repair.setStudentName("张三");
        repair.setStudentNo("20260001");
        repair.setReason("水龙头漏水");
        repair.setPhotoUrl("/uploads/repair/photo.jpg");
        repair.setDormBuilding("3栋");
        repair.setDormRoom("502");
        repair.setStatus(status);
        return repair;
    }
}
