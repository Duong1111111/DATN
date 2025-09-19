package com.example.DATN.service.impls;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public byte[] downloadFile(String filePath) throws IOException {
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, filePath));
        if (blob == null) {
            throw new IOException("File not found: " + filePath);
        }
        return blob.getContent();
    }
    public List<String> listFiles(String folderPath) {
        List<String> fileNames = new ArrayList<>();

        Page<Blob> blobs = storage.list(
                BUCKET_NAME,
                Storage.BlobListOption.prefix(folderPath + "/")
        );

        for (Blob blob : blobs.iterateAll()) {
            fileNames.add(blob.getName());
        }

        return fileNames;
    }

    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        // ✅ Lấy tên file gốc
        String originalFilename = file.getOriginalFilename();

        // ✅ Sinh tên file duy nhất (tránh trùng trên GCS)
        String uniqueFilename = generateUniqueFilename(folderPath, originalFilename);
        String fileName = folderPath + "/" + uniqueFilename;

        // ✅ Tạo Blob và upload
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // ✅ Trả về URL public của file
        return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
    }

    private String generateUniqueFilename(String folder, String originalFilename) {
        String baseName = com.google.common.io.Files.getNameWithoutExtension(originalFilename);
        String extension = com.google.common.io.Files.getFileExtension(originalFilename);

        String newFilename = originalFilename;
        int counter = 1;

        while (fileExistsOnGCS(folder + "/" + newFilename)) {
            newFilename = baseName + "(" + counter + ")" + (extension.isEmpty() ? "" : "." + extension);
            counter++;
        }

        return newFilename;
    }

    private boolean fileExistsOnGCS(String filePath) {
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, filePath));
        return blob != null && blob.exists();
    }

    public boolean deleteFile(String filePath) {
        BlobId blobId = BlobId.of(BUCKET_NAME, filePath);
        return storage.delete(blobId);
    }
}