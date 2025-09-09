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
    private final String BUCKET_NAME = "travelsuggest";

//    public ImageUploadService() throws IOException {
//        try (FileInputStream serviceAccountStream =
//                     new FileInputStream("E:/Lms/gen-lang-client-0154921035-b9cccfbfbca2.json")) {
//
//            this.storage = StorageOptions.newBuilder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
//                    .build()
//                    .getService();
//        }
//    }
//
    public ImageUploadService(Storage storage) {
        this.storage = storage;
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