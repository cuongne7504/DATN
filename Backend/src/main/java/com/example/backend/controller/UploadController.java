package com.example.backend.controller;

import com.example.backend.config.UploadPaths;
import com.example.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Vui lòng chọn một tệp để tải lên"));
        }

        try {
            Path uploadPath = UploadPaths.resolveWritableDir();
            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Tải tệp lên thành công", fileUrl));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Không thể tải tệp lên: " + e.getMessage()));
        }
    }
}
