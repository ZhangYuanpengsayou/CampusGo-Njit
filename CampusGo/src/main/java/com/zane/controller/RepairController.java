package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import com.zane.dto.RepairAuditDTO;
import com.zane.dto.RepairCreateDTO;
import com.zane.dto.RepairRateDTO;
import com.zane.dto.RepairStatusDTO;
import com.zane.exception.BusinessException;
import com.zane.service.RepairService;
import com.zane.vo.PageVO;
import com.zane.vo.RepairApplicationVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping
    @RequireRole(RoleConstants.STUDENT)
    public Result<RepairApplicationVO> createRepair(@RequestBody RepairCreateDTO createDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("提交成功", repairService.createRepair(loginUser.getId(), createDTO));
    }

    @GetMapping("/my")
    @RequireRole(RoleConstants.STUDENT)
    public Result<PageVO<RepairApplicationVO>> listMyRepairs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success(repairService.listMyRepairs(loginUser.getId(), page, pageSize, status));
    }

    @PutMapping("/{id}/cancel")
    @RequireRole(RoleConstants.STUDENT)
    public Result<Void> cancelRepair(@PathVariable Long id, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        repairService.cancelRepair(loginUser.getId(), id);
        return Result.success("撤销成功", null);
    }

    @GetMapping("/audit")
    @RequireRole(RoleConstants.ADMIN)
    public Result<PageVO<RepairApplicationVO>> listAuditRepairs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String dormBuilding) {
        return Result.success(repairService.listAuditRepairs(page, pageSize, status, studentName, dormBuilding));
    }

    @PutMapping("/{id}/audit")
    @RequireRole(RoleConstants.ADMIN)
    public Result<RepairApplicationVO> auditRepair(@PathVariable Long id, @RequestBody RepairAuditDTO auditDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("审核成功", repairService.auditRepair(loginUser.getId(), id, auditDTO));
    }

    @PutMapping("/{id}/status")
    @RequireRole(RoleConstants.ADMIN)
    public Result<RepairApplicationVO> updateRepairStatus(@PathVariable Long id, @RequestBody RepairStatusDTO statusDTO) {
        return Result.success("状态更新成功", repairService.updateRepairStatus(id, statusDTO));
    }

    @PutMapping("/{id}/complete")
    @RequireRole(RoleConstants.STUDENT)
    public Result<RepairApplicationVO> completeRepair(@PathVariable Long id, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("确认完成成功", repairService.completeRepair(loginUser.getId(), id));
    }

    @PutMapping("/{id}/rate")
    @RequireRole(RoleConstants.STUDENT)
    public Result<RepairApplicationVO> rateRepair(@PathVariable Long id, @RequestBody RepairRateDTO rateDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("评价成功", repairService.rateRepair(loginUser.getId(), id, rateDTO));
    }

    private LoginUser getLoginUser(HttpSession session) {
        LoginUser loginUser = (LoginUser) session.getAttribute(SessionConstants.CURRENT_USER);
        if (loginUser == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser;
    }
}
