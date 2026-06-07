package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.common.constant.SessionConstants;
import com.zane.common.session.LoginUser;
import com.zane.dto.AnnouncementDTO;
import com.zane.exception.BusinessException;
import com.zane.service.AnnouncementService;
import com.zane.vo.AnnouncementVO;
import com.zane.vo.PageVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public Result<PageVO<AnnouncementVO>> listAnnouncements(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(announcementService.listAnnouncements(page, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<AnnouncementVO> getAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.getAnnouncement(id));
    }

    @PostMapping
    @RequireRole(RoleConstants.ADMIN)
    public Result<AnnouncementVO> createAnnouncement(@RequestBody AnnouncementDTO announcementDTO, HttpSession session) {
        LoginUser loginUser = getLoginUser(session);
        return Result.success("新增成功", announcementService.createAnnouncement(loginUser.getId(), announcementDTO));
    }

    @PutMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<AnnouncementVO> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementDTO announcementDTO) {
        return Result.success("修改成功", announcementService.updateAnnouncement(id, announcementDTO));
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success("删除成功", null);
    }

    private LoginUser getLoginUser(HttpSession session) {
        LoginUser loginUser = (LoginUser) session.getAttribute(SessionConstants.CURRENT_USER);
        if (loginUser == null) {
            throw new BusinessException(401, "请先登录");
        }
        return loginUser;
    }
}
