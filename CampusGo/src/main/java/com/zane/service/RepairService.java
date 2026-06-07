package com.zane.service;

import com.zane.dto.RepairAuditDTO;
import com.zane.dto.RepairCreateDTO;
import com.zane.dto.RepairRateDTO;
import com.zane.dto.RepairStatusDTO;
import com.zane.vo.PageVO;
import com.zane.vo.RepairApplicationVO;

public interface RepairService {

    RepairApplicationVO createRepair(Long studentId, RepairCreateDTO createDTO);

    PageVO<RepairApplicationVO> listMyRepairs(Long studentId, Integer page, Integer pageSize, String status);

    void cancelRepair(Long studentId, Long repairId);

    PageVO<RepairApplicationVO> listAuditRepairs(Integer page, Integer pageSize, String status, String studentName, String dormBuilding);

    RepairApplicationVO auditRepair(Long adminId, Long repairId, RepairAuditDTO auditDTO);

    RepairApplicationVO updateRepairStatus(Long repairId, RepairStatusDTO statusDTO);

    RepairApplicationVO completeRepair(Long studentId, Long repairId);

    RepairApplicationVO rateRepair(Long studentId, Long repairId, RepairRateDTO rateDTO);
}
