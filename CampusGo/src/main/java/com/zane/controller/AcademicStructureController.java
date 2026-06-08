package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.dto.ClassGroupDTO;
import com.zane.dto.MajorDTO;
import com.zane.service.AcademicStructureService;
import com.zane.vo.ClassGroupVO;
import com.zane.vo.MajorVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicStructureController {

    private final AcademicStructureService academicStructureService;

    public AcademicStructureController(AcademicStructureService academicStructureService) {
        this.academicStructureService = academicStructureService;
    }

    @GetMapping("/majors")
    public Result<List<MajorVO>> listEnabledMajors(@RequestParam String college) {
        return Result.success(academicStructureService.listEnabledMajors(college));
    }

    @GetMapping("/majors/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<MajorVO>> listAllMajors() {
        return Result.success(academicStructureService.listAllMajors());
    }

    @PostMapping("/majors")
    @RequireRole(RoleConstants.ADMIN)
    public Result<MajorVO> createMajor(@RequestBody MajorDTO majorDTO) {
        return Result.success("新增成功", academicStructureService.createMajor(majorDTO));
    }

    @PutMapping("/majors/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<MajorVO> updateMajor(@PathVariable Long id, @RequestBody MajorDTO majorDTO) {
        return Result.success("修改成功", academicStructureService.updateMajor(id, majorDTO));
    }

    @DeleteMapping("/majors/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteMajor(@PathVariable Long id) {
        academicStructureService.deleteMajor(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/classes")
    public Result<List<ClassGroupVO>> listEnabledClasses(@RequestParam String college, @RequestParam String major) {
        return Result.success(academicStructureService.listEnabledClasses(college, major));
    }

    @GetMapping("/classes/manage")
    @RequireRole(RoleConstants.ADMIN)
    public Result<List<ClassGroupVO>> listAllClasses() {
        return Result.success(academicStructureService.listAllClasses());
    }

    @PostMapping("/classes")
    @RequireRole(RoleConstants.ADMIN)
    public Result<ClassGroupVO> createClassGroup(@RequestBody ClassGroupDTO classGroupDTO) {
        return Result.success("新增成功", academicStructureService.createClassGroup(classGroupDTO));
    }

    @PutMapping("/classes/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<ClassGroupVO> updateClassGroup(@PathVariable Long id, @RequestBody ClassGroupDTO classGroupDTO) {
        return Result.success("修改成功", academicStructureService.updateClassGroup(id, classGroupDTO));
    }

    @DeleteMapping("/classes/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteClassGroup(@PathVariable Long id) {
        academicStructureService.deleteClassGroup(id);
        return Result.success("删除成功", null);
    }
}
