package com.example.DATN.controller;


import com.example.DATN.service.impls.ImageUploadService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(defaultValue = "documents") String folder) throws IOException {
//        String url = imageUploadService.uploadImage(file, folder);
//        return ResponseEntity.ok(url);
//    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("folder") String folder) {
        try {
            String fileUrl = imageUploadService.uploadFile(file, folder);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to store file: " + e.getMessage());
        }
    }

    // Download file theo đường dẫn trong bucket
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) throws IOException {
        byte[] content = imageUploadService.downloadFile(filePath);

        String fileName = Paths.get(filePath).getFileName().toString();
        String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

        MediaType mediaType;
        if (fileName.toLowerCase().endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (fileName.toLowerCase().endsWith(".doc")) {
            mediaType = MediaType.parseMediaType("application/msword");
        } else if (fileName.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            mediaType = MediaType.IMAGE_PNG;
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        Resource resource = new ByteArrayResource(content);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName)
                .contentType(mediaType)
                .contentLength(content.length)
                .body(resource);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam String folder) {
        List<String> files = imageUploadService.listFiles(folder);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            boolean deleted = imageUploadService.deleteFile(filePath);
            if (deleted) {
                return ResponseEntity.ok("File deleted successfully: " + filePath);
            } else {
                return ResponseEntity.status(404).body("File not found: " + filePath);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
        }
    }
}