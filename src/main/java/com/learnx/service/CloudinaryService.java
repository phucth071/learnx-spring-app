package com.learnx.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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

    public String uploadRemainFileName(MultipartFile file) throws IOException {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String filenameWithoutExtension = originalFilename.replaceFirst("[.][^.]+$", "");
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "raw", "public_id", filenameWithoutExtension + "." + fileExtension));
        return uploadResult.get("url").toString();
    }
}