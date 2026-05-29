package com.mukesh.netflix.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // 1. படங்கள் (Thumbnails / Banners / Avatars) அப்லோட் செய்ய
    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "netflix_clone/" + folderName,
                        "resource_type", "image"
                ));
        return uploadResult.get("secure_url").toString(); // Cloudinary Secure URL-ஐ ரிட்டன் செய்யும்
    }

    // 2. வீடியோ ஃபைல்களை அப்லோட் செய்ய (Netflix Movies / Teasers)
    public String uploadVideo(MultipartFile file, String folderName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "netflix_clone/" + folderName,
                        "resource_type", "video",
                        "chunk_size", 6000000 // பெரிய வீடியோக்களுக்கான சங்க் சைஸ் (6MB)
                ));
        return uploadResult.get("secure_url").toString();
    }
}
