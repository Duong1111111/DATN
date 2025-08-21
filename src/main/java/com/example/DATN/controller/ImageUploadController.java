package com.example.DATN.controller;


import com.example.DATN.service.impls.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Vui lòng chọn một file ảnh."));
        }
        try {
            // Truyền folder mặc định, ví dụ "uploads/"
            String imageUrl = imageUploadService.uploadImage(file, "uploads/");

            // Trả về một đối tượng JSON chứa URL
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi khi upload ảnh: " + e.getMessage()));
        }
    }
}