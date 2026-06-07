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
import com.zane.service.RepairService;
import com.zane.vo.PageVO;
import com.zane.vo.RepairApplicationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class RepairServiceImpl implements RepairService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Set<String> REPAIR_STATUSES = Set.of(
            StatusConstants.Repair.PENDING,
            StatusConstants.Repair.APPROVED,
            StatusConstants.Repair.REJECTED,
            StatusConstants.Repair.CANCELED,
            StatusConstants.Repair.REPAIRING,
            StatusConstants.Repair.COMPLETED,
            StatusConstants.Repair.RATED
    );

    private final RepairApplicationMapper repairApplicationMapper;
    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;

    public RepairServiceImpl(
            RepairApplicationMapper repairApplicationMapper,
            UserMapper userMapper,
            StudentProfileMapper studentProfileMapper) {
        this.repairApplicationMapper = repairApplicationMapper;
        this.userMapper = userMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    @Transactional
    public RepairApplicationVO createRepair(Long studentId, RepairCreateDTO createDTO) {
        validateCreateDTO(createDTO);
        User student = getUser(studentId);
        if (!RoleConstants.STUDENT.equals(student.getRole())) {
            throw new BusinessException(403, "只有学生可以提交报修申请");
        }

        StudentProfile profile = studentProfileMapper.findByUserId(studentId);
        RepairApplication repairApplication = new RepairApplication();
        repairApplication.setStudentId(studentId);
        repairApplication.setReason(createDTO.getReason().trim());
        repairApplication.setPhotoUrl(trimToNull(createDTO.getPhotoUrl()));
        repairApplication.setDormBuilding(profile == null ? null : profile.getDormBuilding());
        repairApplication.setDormRoom(profile == null ? null : profile.getDormRoom());
        repairApplication.setStatus(StatusConstants.Repair.PENDING);
        repairApplicationMapper.insert(repairApplication);
        return getRepairVO(repairApplication.getId());
    }

    @Override
    public PageVO<RepairApplicationVO> listMyRepairs(Long studentId, Integer page, Integer pageSize, String status) {
        String normalizedStatus = normalizeStatus(status);
        int currentPage = normalizePage(page);
        int size = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * size;

        long total = repairApplicationMapper.countByStudent(studentId, normalizedStatus);
        List<RepairApplicationVO> list = repairApplicationMapper.findByStudentPage(studentId, normalizedStatus, offset, size)
                .stream()
                .map(this::toVO)
                .toList();
        return new PageVO<>(total, list);
    }

    @Override
    @Transactional
    public void cancelRepair(Long studentId, Long repairId) {
        RepairApplication repairApplication = getExistingRepair(repairId);
        if (!studentId.equals(repairApplication.getStudentId())) {
            throw new BusinessException(403, "只能撤销自己的报修申请");
        }
        if (!StatusConstants.Repair.PENDING.equals(repairApplication.getStatus())) {
            throw new BusinessException("只有待审核的报修申请可以撤销");
        }
        repairApplicationMapper.cancelByStudent(repairId, studentId);
    }

    @Override
    public PageVO<RepairApplicationVO> listAuditRepairs(Integer page, Integer pageSize, String status, String studentName, String dormBuilding) {
        String normalizedStatus = normalizeStatus(status);
        String normalizedStudentName = trimToNull(studentName);
        String normalizedDormBuilding = trimToNull(dormBuilding);
        int currentPage = normalizePage(page);
        int size = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * size;

        long total = repairApplicationMapper.countAudit(normalizedStatus, normalizedStudentName, normalizedDormBuilding);
        List<RepairApplicationVO> list = repairApplicationMapper
                .findAuditPage(normalizedStatus, normalizedStudentName, normalizedDormBuilding, offset, size)
                .stream()
                .map(this::toVO)
                .toList();
        return new PageVO<>(total, list);
    }

    @Override
    @Transactional
    public RepairApplicationVO auditRepair(Long adminId, Long repairId, RepairAuditDTO auditDTO) {
        validateAuditDTO(auditDTO);
        User admin = getAdmin(adminId);
        RepairApplication repairApplication = getExistingRepair(repairId);
        if (!StatusConstants.Repair.PENDING.equals(repairApplication.getStatus())) {
            throw new BusinessException("只有待审核的报修申请可以审核");
        }

        String repairmanPhone = trimToNull(auditDTO.getRepairmanPhone());
        if (StatusConstants.Repair.APPROVED.equals(auditDTO.getStatus())
                && (repairmanPhone == null || !PHONE_PATTERN.matcher(repairmanPhone).matches())) {
            throw new BusinessException("审核通过时必须填写正确的维修工手机号");
        }
        repairApplicationMapper.auditByAdmin(
                repairId,
                admin.getId(),
                auditDTO.getStatus(),
                repairmanPhone,
                trimToNull(auditDTO.getAuditOpinion())
        );
        return getRepairVO(repairId);
    }

    @Override
    @Transactional
    public RepairApplicationVO updateRepairStatus(Long repairId, RepairStatusDTO statusDTO) {
        if (statusDTO == null || !StringUtils.hasText(statusDTO.getStatus())) {
            throw new BusinessException("维修状态不能为空");
        }
        String targetStatus = statusDTO.getStatus().trim();
        if (!StatusConstants.Repair.REPAIRING.equals(targetStatus)) {
            throw new BusinessException("管理员只能将报修状态更新为维修中");
        }

        RepairApplication repairApplication = getExistingRepair(repairId);
        if (!StatusConstants.Repair.APPROVED.equals(repairApplication.getStatus())) {
            throw new BusinessException("只有审核通过的报修可以进入维修中");
        }

        repairApplicationMapper.updateStatus(repairId, targetStatus);
        return getRepairVO(repairId);
    }

    @Override
    @Transactional
    public RepairApplicationVO completeRepair(Long studentId, Long repairId) {
        RepairApplication repairApplication = getExistingRepair(repairId);
        if (!studentId.equals(repairApplication.getStudentId())) {
            throw new BusinessException(403, "只能确认完成自己的报修申请");
        }
        if (!StatusConstants.Repair.APPROVED.equals(repairApplication.getStatus())
                && !StatusConstants.Repair.REPAIRING.equals(repairApplication.getStatus())) {
            throw new BusinessException("只有已通过或维修中的报修申请可以确认完成");
        }

        repairApplicationMapper.completeByStudent(repairId, studentId);
        return getRepairVO(repairId);
    }

    @Override
    @Transactional
    public RepairApplicationVO rateRepair(Long studentId, Long repairId, RepairRateDTO rateDTO) {
        validateRateDTO(rateDTO);
        RepairApplication repairApplication = getExistingRepair(repairId);
        if (!studentId.equals(repairApplication.getStudentId())) {
            throw new BusinessException(403, "只能评价自己的报修申请");
        }
        if (!StatusConstants.Repair.COMPLETED.equals(repairApplication.getStatus())) {
            throw new BusinessException("只有已完成的报修申请可以评价");
        }

        repairApplicationMapper.rateByStudent(repairId, studentId, rateDTO.getScore(), trimToNull(rateDTO.getComment()));
        return getRepairVO(repairId);
    }

    private void validateCreateDTO(RepairCreateDTO createDTO) {
        if (createDTO == null) {
            throw new BusinessException("报修申请不能为空");
        }
        if (!StringUtils.hasText(createDTO.getReason())) {
            throw new BusinessException("报修事由不能为空");
        }
        if (createDTO.getReason().trim().length() > 500) {
            throw new BusinessException("报修事由不能超过 500 个字符");
        }
    }

    private void validateAuditDTO(RepairAuditDTO auditDTO) {
        if (auditDTO == null || !StringUtils.hasText(auditDTO.getStatus())) {
            throw new BusinessException("审核结果不能为空");
        }
        if (!StatusConstants.Repair.APPROVED.equals(auditDTO.getStatus())
                && !StatusConstants.Repair.REJECTED.equals(auditDTO.getStatus())) {
            throw new BusinessException("审核状态只能为通过或不通过");
        }
    }

    private void validateRateDTO(RepairRateDTO rateDTO) {
        if (rateDTO == null || rateDTO.getScore() == null) {
            throw new BusinessException("评分不能为空");
        }
        if (rateDTO.getScore() < 1 || rateDTO.getScore() > 5) {
            throw new BusinessException("评分范围为 1 到 5");
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        String normalizedStatus = status.trim();
        if (!REPAIR_STATUSES.contains(normalizedStatus)) {
            throw new BusinessException("报修状态不正确");
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

    private User getAdmin(Long adminId) {
        User admin = getUser(adminId);
        if (!RoleConstants.ADMIN.equals(admin.getRole())) {
            throw new BusinessException(403, "只有管理员可以审核报修申请");
        }
        return admin;
    }

    private RepairApplication getExistingRepair(Long repairId) {
        if (repairId == null || repairId <= 0) {
            throw new BusinessException("报修申请 ID 不正确");
        }
        RepairApplication repairApplication = repairApplicationMapper.findById(repairId);
        if (repairApplication == null) {
            throw new BusinessException(404, "报修申请不存在");
        }
        return repairApplication;
    }

    private RepairApplicationVO getRepairVO(Long repairId) {
        return toVO(getExistingRepair(repairId));
    }

    private RepairApplicationVO toVO(RepairApplication repairApplication) {
        RepairApplicationVO vo = new RepairApplicationVO();
        vo.setId(repairApplication.getId());
        vo.setStudentId(repairApplication.getStudentId());
        vo.setStudentName(repairApplication.getStudentName());
        vo.setStudentNo(repairApplication.getStudentNo());
        vo.setReason(repairApplication.getReason());
        vo.setPhotoUrl(repairApplication.getPhotoUrl());
        vo.setDormBuilding(repairApplication.getDormBuilding());
        vo.setDormRoom(repairApplication.getDormRoom());
        vo.setStatus(repairApplication.getStatus());
        vo.setAuditorId(repairApplication.getAuditorId());
        vo.setAuditorName(repairApplication.getAuditorName());
        vo.setAuditOpinion(repairApplication.getAuditOpinion());
        vo.setAuditTime(repairApplication.getAuditTime());
        vo.setRepairmanPhone(repairApplication.getRepairmanPhone());
        vo.setScore(repairApplication.getScore());
        vo.setComment(repairApplication.getComment());
        vo.setCommentTime(repairApplication.getCommentTime());
        vo.setCreatedAt(repairApplication.getCreatedAt());
        vo.setUpdatedAt(repairApplication.getUpdatedAt());
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
