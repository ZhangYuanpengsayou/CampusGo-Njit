package com.zane.service;

import com.zane.dto.LeaveAuditDTO;
import com.zane.dto.LeaveCreateDTO;
import com.zane.vo.LeaveApplicationVO;
import com.zane.vo.PageVO;

public interface LeaveService {

    LeaveApplicationVO createLeave(Long studentId, LeaveCreateDTO createDTO);

    PageVO<LeaveApplicationVO> listMyLeaves(Long studentId, Integer page, Integer pageSize, String status);

    void cancelLeave(Long studentId, Long leaveId);

    void returnLeave(Long studentId, Long leaveId);

    PageVO<LeaveApplicationVO> listAuditLeaves(Long teacherId, Integer page, Integer pageSize, String status, String studentName);

    LeaveApplicationVO auditLeave(Long teacherId, Long leaveId, LeaveAuditDTO auditDTO);
}
