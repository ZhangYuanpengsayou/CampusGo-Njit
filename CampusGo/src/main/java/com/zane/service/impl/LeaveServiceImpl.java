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
import com.zane.service.LeaveService;
import com.zane.vo.LeaveApplicationVO;
import com.zane.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class LeaveServiceImpl implements LeaveService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> LEAVE_STATUSES = Set.of(
            StatusConstants.Leave.PENDING,
            StatusConstants.Leave.APPROVED,
            StatusConstants.Leave.REJECTED,
            StatusConstants.Leave.CANCELED,
            StatusConstants.Leave.RETURNED
    );

    private final LeaveApplicationMapper leaveApplicationMapper;
    private final UserMapper userMapper;

    public LeaveServiceImpl(LeaveApplicationMapper leaveApplicationMapper, UserMapper userMapper) {
        this.leaveApplicationMapper = leaveApplicationMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public LeaveApplicationVO createLeave(Long studentId, LeaveCreateDTO createDTO) {
        validateCreateDTO(createDTO);
        User student = getUser(studentId);
        if (!RoleConstants.STUDENT.equals(student.getRole())) {
            throw new BusinessException(403, "只有学生可以提交请假申请");
        }
        if (!StringUtils.hasText(student.getCollege())) {
            throw new BusinessException("学生学院信息不能为空");
        }

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setStudentId(studentId);
        leaveApplication.setCollege(student.getCollege());
        leaveApplication.setReason(createDTO.getReason().trim());
        leaveApplication.setStartTime(createDTO.getStartTime());
        leaveApplication.setEndTime(createDTO.getEndTime());
        leaveApplication.setStatus(StatusConstants.Leave.PENDING);
        leaveApplicationMapper.insert(leaveApplication);
        return getLeaveVO(leaveApplication.getId());
    }

    @Override
    public PageVO<LeaveApplicationVO> listMyLeaves(Long studentId, Integer page, Integer pageSize, String status) {
        String normalizedStatus = normalizeStatus(status);
        int currentPage = normalizePage(page);
        int size = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * size;

        long total = leaveApplicationMapper.countByStudent(studentId, normalizedStatus);
        List<LeaveApplicationVO> list = leaveApplicationMapper.findByStudentPage(studentId, normalizedStatus, offset, size)
                .stream()
                .map(this::toVO)
                .toList();
        return new PageVO<>(total, list);
    }

    @Override
    @Transactional
    public void cancelLeave(Long studentId, Long leaveId) {
        LeaveApplication leaveApplication = getExistingLeave(leaveId);
        if (!studentId.equals(leaveApplication.getStudentId())) {
            throw new BusinessException(403, "只能撤销自己的请假申请");
        }
        if (!StatusConstants.Leave.PENDING.equals(leaveApplication.getStatus())) {
            throw new BusinessException("只有待审核的请假申请可以撤销");
        }
        leaveApplicationMapper.cancelByStudent(leaveId, studentId);
    }

    @Override
    @Transactional
    public void returnLeave(Long studentId, Long leaveId) {
        LeaveApplication leaveApplication = getExistingLeave(leaveId);
        if (!studentId.equals(leaveApplication.getStudentId())) {
            throw new BusinessException(403, "只能销假自己的请假申请");
        }
        if (!StatusConstants.Leave.APPROVED.equals(leaveApplication.getStatus())) {
            throw new BusinessException("只有审核通过的请假申请可以销假");
        }
        leaveApplicationMapper.returnByStudent(leaveId, studentId);
    }

    @Override
    public PageVO<LeaveApplicationVO> listAuditLeaves(Long teacherId, Integer page, Integer pageSize, String status, String studentName) {
        User teacher = getTeacher(teacherId);
        String normalizedStatus = normalizeStatus(status);
        String normalizedStudentName = trimToNull(studentName);
        int currentPage = normalizePage(page);
        int size = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * size;

        long total = leaveApplicationMapper.countByCollege(teacher.getCollege(), normalizedStatus, normalizedStudentName);
        List<LeaveApplicationVO> list = leaveApplicationMapper
                .findByCollegePage(teacher.getCollege(), normalizedStatus, normalizedStudentName, offset, size)
                .stream()
                .map(this::toVO)
                .toList();
        return new PageVO<>(total, list);
    }

    @Override
    @Transactional
    public LeaveApplicationVO auditLeave(Long teacherId, Long leaveId, LeaveAuditDTO auditDTO) {
        validateAuditDTO(auditDTO);
        User teacher = getTeacher(teacherId);
        LeaveApplication leaveApplication = getExistingLeave(leaveId);
        if (!teacher.getCollege().equals(leaveApplication.getCollege())) {
            throw new BusinessException(403, "只能审核本学院学生的请假申请");
        }
        if (!StatusConstants.Leave.PENDING.equals(leaveApplication.getStatus())) {
            throw new BusinessException("只有待审核的请假申请可以审核");
        }

        leaveApplicationMapper.auditByTeacher(
                leaveId,
                teacher.getCollege(),
                teacherId,
                auditDTO.getStatus(),
                trimToNull(auditDTO.getAuditOpinion())
        );
        return getLeaveVO(leaveId);
    }

    private void validateCreateDTO(LeaveCreateDTO createDTO) {
        if (createDTO == null) {
            throw new BusinessException("请假申请不能为空");
        }
        if (!StringUtils.hasText(createDTO.getReason())) {
            throw new BusinessException("请假理由不能为空");
        }
        if (createDTO.getStartTime() == null || createDTO.getEndTime() == null) {
            throw new BusinessException("请假开始时间和结束时间不能为空");
        }
        if (createDTO.getStartTime().isAfter(createDTO.getEndTime())) {
            throw new BusinessException("请假开始时间不能晚于结束时间");
        }
    }

    private void validateAuditDTO(LeaveAuditDTO auditDTO) {
        if (auditDTO == null || !StringUtils.hasText(auditDTO.getStatus())) {
            throw new BusinessException("审核结果不能为空");
        }
        if (!StatusConstants.Leave.APPROVED.equals(auditDTO.getStatus())
                && !StatusConstants.Leave.REJECTED.equals(auditDTO.getStatus())) {
            throw new BusinessException("审核状态只能为通过或不通过");
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        String normalizedStatus = status.trim();
        if (!LEAVE_STATUSES.contains(normalizedStatus)) {
            throw new BusinessException("请假状态不正确");
        }
        return normalizedStatus;
    }

    private User getUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private User getTeacher(Long teacherId) {
        User teacher = getUser(teacherId);
        if (!RoleConstants.TEACHER.equals(teacher.getRole())) {
            throw new BusinessException(403, "只有教师可以审核请假申请");
        }
        if (!StringUtils.hasText(teacher.getCollege())) {
            throw new BusinessException("教师学院信息不能为空");
        }
        return teacher;
    }

    private LeaveApplication getExistingLeave(Long leaveId) {
        if (leaveId == null || leaveId <= 0) {
            throw new BusinessException("请假申请 ID 不正确");
        }
        LeaveApplication leaveApplication = leaveApplicationMapper.findById(leaveId);
        if (leaveApplication == null) {
            throw new BusinessException(404, "请假申请不存在");
        }
        return leaveApplication;
    }

    private LeaveApplicationVO getLeaveVO(Long leaveId) {
        return toVO(getExistingLeave(leaveId));
    }

    private LeaveApplicationVO toVO(LeaveApplication leaveApplication) {
        LeaveApplicationVO vo = new LeaveApplicationVO();
        vo.setId(leaveApplication.getId());
        vo.setStudentId(leaveApplication.getStudentId());
        vo.setStudentName(leaveApplication.getStudentName());
        vo.setStudentNo(leaveApplication.getStudentNo());
        vo.setCollege(leaveApplication.getCollege());
        vo.setReason(leaveApplication.getReason());
        vo.setStartTime(leaveApplication.getStartTime());
        vo.setEndTime(leaveApplication.getEndTime());
        vo.setStatus(leaveApplication.getStatus());
        vo.setAuditorId(leaveApplication.getAuditorId());
        vo.setAuditorName(leaveApplication.getAuditorName());
        vo.setAuditOpinion(leaveApplication.getAuditOpinion());
        vo.setAuditTime(leaveApplication.getAuditTime());
        vo.setReturnTime(leaveApplication.getReturnTime());
        vo.setCreatedAt(leaveApplication.getCreatedAt());
        vo.setUpdatedAt(leaveApplication.getUpdatedAt());
        return vo;
    }

    private int normalizePage(Integer page) {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
