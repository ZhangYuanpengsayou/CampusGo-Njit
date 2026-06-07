package com.zane.service.impl;

import com.zane.exception.BusinessException;
import com.zane.service.FileStorageService;
import com.zane.vo.FileUploadVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Path UPLOAD_ROOT = Path.of("uploads");
    private static final Path REPAIR_UPLOAD_ROOT = UPLOAD_ROOT.resolve("repair");
    private static final Path CAROUSEL_UPLOAD_ROOT = UPLOAD_ROOT.resolve("carousel");

    @Override
    public FileUploadVO uploadRepairPhoto(MultipartFile file) {
        return storeImage(file, REPAIR_UPLOAD_ROOT, "/uploads/repair/", "报修照片上传失败");
    }

    @Override
    public FileUploadVO uploadCarouselImage(MultipartFile file) {
        return storeImage(file, CAROUSEL_UPLOAD_ROOT, "/uploads/carousel/", "轮播图上传失败");
    }

    private FileUploadVO storeImage(MultipartFile file, Path uploadRoot, String publicPrefix, String errorMessage) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String filename = UUID.randomUUID() + "." + extension;
        Path targetDir = uploadRoot.resolve(datePath).normalize();
        Path targetFile = targetDir.resolve(filename).normalize();

        if (!targetFile.startsWith(uploadRoot.normalize())) {
            throw new BusinessException("文件路径不正确");
        }

        try {
            Files.createDirectories(targetDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new BusinessException(500, errorMessage);
        }

        return new FileUploadVO(publicPrefix + datePath + "/" + filename);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的照片");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("照片大小不能超过 5MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持 jpg、jpeg、png 格式照片");
        }
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType) && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException("照片类型不正确");
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            throw new BusinessException("照片文件名不正确");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
