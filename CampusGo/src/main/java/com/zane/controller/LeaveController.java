package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import com.zane.dto.LeaveAuditDTO;
import com.zane.dto.LeaveCreateDTO;
import com.zane.exception.BusinessException;
import com.zane.service.LeaveService;
import com.zane.vo.LeaveApplicationVO;
import com.zane.vo.PageVO;
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
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    @RequireRole(RoleConstants.STUDENT)
    public Result<LeaveApplicationVO> createLeave(@RequestBody LeaveCreateDTO createDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("提交成功", leaveService.createLeave(loginUser.getId(), createDTO));
    }

    @GetMapping("/my")
    @RequireRole(RoleConstants.STUDENT)
    public Result<PageVO<LeaveApplicationVO>> listMyLeaves(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success(leaveService.listMyLeaves(loginUser.getId(), page, pageSize, status));
    }

    @PutMapping("/{id}/cancel")
    @RequireRole(RoleConstants.STUDENT)
    public Result<Void> cancelLeave(@PathVariable Long id, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        leaveService.cancelLeave(loginUser.getId(), id);
        return Result.success("撤销成功", null);
    }

    @PutMapping("/{id}/return")
    @RequireRole(RoleConstants.STUDENT)
    public Result<Void> returnLeave(@PathVariable Long id, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        leaveService.returnLeave(loginUser.getId(), id);
        return Result.success("销假成功", null);
    }

    @GetMapping("/audit")
    @RequireRole(RoleConstants.TEACHER)
    public Result<PageVO<LeaveApplicationVO>> listAuditLeaves(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String studentName,
            HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success(leaveService.listAuditLeaves(loginUser.getId(), page, pageSize, status, studentName));
    }

    @PutMapping("/{id}/audit")
    @RequireRole(RoleConstants.TEACHER)
    public Result<LeaveApplicationVO> auditLeave(@PathVariable Long id, @RequestBody LeaveAuditDTO auditDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("审核成功", leaveService.auditLeave(loginUser.getId(), id, auditDTO));
    }

    private LoginUser getLoginUser(HttpSession session) {
        LoginUser loginUser = (LoginUser) session.getAttribute(SessionConstants.CURRENT_USER);
        if (loginUser == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser;
    }
}
