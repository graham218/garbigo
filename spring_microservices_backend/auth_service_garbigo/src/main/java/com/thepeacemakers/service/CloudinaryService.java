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
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "folder", "garbigo/profiles",
                    "resource_type", "image",
                    "transformation", new Object[]{
                        ObjectUtils.asMap("width", 500, "height", 500, "crop", "fill"),
                        ObjectUtils.asMap("gravity", "face", "crop", "crop"),
                        ObjectUtils.asMap("radius", "max")
                    }
                ));
            
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image");
        }
    }
    
    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary", e);
        }
    }
    
    private String extractPublicIdFromUrl(String url) {
        try {
            // Cloudinary URL pattern: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/filename.jpg
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Remove version if present
                if (path.startsWith("v")) {
                    path = path.substring(path.indexOf('/') + 1);
                }
                // Remove file extension
                int lastDot = path.lastIndexOf('.');
                if (lastDot != -1) {
                    path = path.substring(0, lastDot);
                }
                return path;
            }
        } catch (Exception e) {
            log.error("Failed to extract public ID from URL: {}", url, e);
        }
        return null;
    }
}