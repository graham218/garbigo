package com.thepeacemakers.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public String uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "garbigo/profiles",
                    "resource_type", "auto"
                )
            );
            
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary: ", e);
            throw new RuntimeException("Failed to upload image");
        }
    }
    
    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from URL
            String publicId = extractPublicId(imageUrl);
            
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
            
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary: ", e);
        }
    }
    
    private String extractPublicId(String imageUrl) {
        try {
            // Cloudinary URL format: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/filename.jpg
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Remove version prefix if exists
                if (path.startsWith("v")) {
                    path = path.substring(path.indexOf("/") + 1);
                }
                // Remove file extension
                int dotIndex = path.lastIndexOf(".");
                if (dotIndex != -1) {
                    path = path.substring(0, dotIndex);
                }
                return path;
            }
        } catch (Exception e) {
            log.error("Failed to extract public ID from URL: {}", imageUrl, e);
        }
        return null;
    }
}