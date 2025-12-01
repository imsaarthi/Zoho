package com.yourcompany.hrms.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String BASE_DIR = "C:/hrms/uploads/";
    private static final String PROFILE_PHOTOS_DIR = BASE_DIR + "profile-photos/";
    private static final String ATTENDANCE_PHOTOS_DIR = BASE_DIR + "attendance-photos/";
    private static final String BASE_URL = "http://192.168.1.27:8080/uploads/";

    public String saveProfileImage(MultipartFile file, Long userId) {
        return saveFile(file, PROFILE_PHOTOS_DIR, "user-" + userId);
    }

    public String saveAttendanceImage(MultipartFile file, Long userId) {
        return saveFile(file, ATTENDANCE_PHOTOS_DIR, String.valueOf(userId));
    }

    private String saveFile(MultipartFile file, String directory, String prefix) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot save empty file");
            }

            Path uploadPath = Paths.get(directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                extension = ".jpg"; // Default to jpg if no extension
            }

            String fileName = prefix + "-" + timestamp + "-" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construct public URL
            String relativePath = directory.replace(BASE_DIR, "").replace("\\", "/");
            return BASE_URL + relativePath + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public void deleteFileIfExists(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(BASE_URL)) {
            return;
        }

        try {
            String relativePath = fileUrl.replace(BASE_URL, "");
            Path filePath = Paths.get(BASE_DIR, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log warning but don't throw exception to avoid breaking the transaction
            System.err.println("Could not delete file: " + fileUrl + ". Error: " + e.getMessage());
        }
    }
}
