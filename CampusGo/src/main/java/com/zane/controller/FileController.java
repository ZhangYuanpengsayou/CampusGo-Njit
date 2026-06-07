package com.zane.controller;

import com.zane.common.Result;
import com.zane.common.annotation.RequireRole;
import com.zane.common.constant.RoleConstants;
import com.zane.service.FileStorageService;
import com.zane.vo.FileUploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/repair-photo")
    @RequireRole(RoleConstants.STUDENT)
    public Result<FileUploadVO> uploadRepairPhoto(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", fileStorageService.uploadRepairPhoto(file));
    }

    @PostMapping("/carousel-image")
    @RequireRole(RoleConstants.ADMIN)
    public Result<FileUploadVO> uploadCarouselImage(@RequestParam("file") MultipartFile file) {
        return Result.success("上传成功", fileStorageService.uploadCarouselImage(file));
    }
}
