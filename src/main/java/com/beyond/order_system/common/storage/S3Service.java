package com.beyond.order_system.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    S3UploadResult upload(MultipartFile file, String keyPrefix);
}
