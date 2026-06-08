package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.dto.CollegeDTO;
import com.zane.service.CollegeService;
import com.zane.vo.CollegeVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public Result<List<CollegeVO>> listEnabledColleges() {
        return Result.success(collegeService.listEnabledColleges());
    }

    @GetMapping("/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<CollegeVO>> listAllColleges() {
        return Result.success(collegeService.listAllColleges());
    }

    @PostMapping
    @RequireRole(RoleConstants.ADMIN)
    public Result<CollegeVO> createCollege(@RequestBody CollegeDTO collegeDTO) {
        return Result.success("新增成功", collegeService.createCollege(collegeDTO));
    }

    @PutMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<CollegeVO> updateCollege(@PathVariable Long id, @RequestBody CollegeDTO collegeDTO) {
        return Result.success("修改成功", collegeService.updateCollege(id, collegeDTO));
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteCollege(@PathVariable Long id) {
        collegeService.deleteCollege(id);
        return Result.success("删除成功", null);
    }
}
