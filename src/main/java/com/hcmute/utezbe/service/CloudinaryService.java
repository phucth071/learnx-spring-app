package com.hcmute.utezbe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        try {
            Map<?, ?> res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) res.get("url");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload");
        }
    }
}
