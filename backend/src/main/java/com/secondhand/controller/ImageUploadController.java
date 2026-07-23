package com.secondhand.controller;

import com.secondhand.service.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * REST controller for handling image file uploads.
 *
 * <p>Provides an endpoint for uploading image files associated with advertisements.
 * The files are stored in the {@code uploads/} directory and the relative path is
 * returned to the client for storage in the advertisement entity.</p>
 */
@RestController
@RequestMapping("api/images")
public class ImageUploadController {

    private static final String UPLOAD_DIR = "uploads/";

    /**
     * Uploads an image file and returns its stored path.
     *
     * <p>The file is saved with a unique filename (UUID) to prevent name collisions.
     * The returned path can be used in the {@code images} field of advertisement
     * creation requests.</p>
     *
     * @param token the JWT bearer token from the {@code Authorization} header
     * @param file  the image file to upload (multipart/form-data)
     * @return the relative path of the stored file
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestHeader("Authorization") String token,
                                              @RequestParam("file") MultipartFile file) {
        JwtUtil.getUserIdFromToken(token);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("فایل انتخاب نشده است");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok(UPLOAD_DIR + fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("خطا در ذخیره فایل");
        }
    }
}