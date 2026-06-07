package com.zane.service;

import com.zane.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileUploadVO uploadRepairPhoto(MultipartFile file);

    FileUploadVO uploadCarouselImage(MultipartFile file);
}
