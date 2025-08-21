package com.example.DATN.service.impls;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final Storage storage;
    private final String BUCKET_NAME = "travelsuggest"; // <-- Tên bucket của bạn

    public ImageUploadService() throws IOException {
        // Đường dẫn tới file key .json của bạn
        // Đảm bảo file này nằm trong thư mục resources của project
        FileInputStream serviceAccountStream = new FileInputStream("src/main/resources/service-account-test.json");

        this.storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build()
                .getService();
    }

    public String uploadImage(MultipartFile file, String folderPath) throws IOException {
        String fileName = folderPath + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
    }
}