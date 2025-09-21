package com.example.DATN.service.impls;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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

    public List<Map<String, String>> listFiles(String folderPath) {
        List<Map<String, String>> results = new ArrayList<>();
        String prefix = folderPath.endsWith("/") ? folderPath : folderPath + "/";

        // Đảm bảo chúng ta đang tìm trong thư mục gốc nếu path là "docs"
        if (folderPath.equals("docs")) {
            prefix = "docs/";
        }

        Page<Blob> blobs = storage.list(
                BUCKET_NAME,
                Storage.BlobListOption.prefix(prefix),
                Storage.BlobListOption.currentDirectory()
        );

        for (Blob blob : blobs.iterateAll()) {
            String name = blob.getName();
            if (name.equals(prefix)) {
                continue;
            }

            Map<String, String> item = new HashMap<>();

            // *** BẮT ĐẦU SỬA LỖI ***
            // Thêm dòng này để luôn trả về đường dẫn đầy đủ
            item.put("path", name);
            // *** KẾT THÚC SỬA LỖI ***

            if (blob.isDirectory()) {
                String dirName = name.substring(prefix.length(), name.length() - 1);
                item.put("name", dirName);
                item.put("type", "directory");
            } else {
                String fileName = name.substring(prefix.length());
                item.put("name", fileName);
                item.put("type", "file");
            }
            results.add(item);
        }
        return results;
    }


    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        // 1. Làm sạch tên file gốc
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // 2. Sinh tên file duy nhất
        String uniqueFilename = generateUniqueFilename(folderPath, originalFilename);

        // 3. Ghép đường dẫn an toàn, luôn dùng "/" cho GCS
        String fullPath = Paths.get(folderPath, uniqueFilename)
                .toString()
                .replace("\\", "/");

        // 4. Tạo Blob và upload
        BlobId blobId = BlobId.of(BUCKET_NAME, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // 5. Trả về URL public (nếu bucket public)
        return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fullPath);
    }

    public boolean createFolder(String folderPath) {
        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }
        BlobId blobId = BlobId.of(BUCKET_NAME, folderPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, new byte[0]);
        return true;
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

    public boolean delete(String path, boolean isFolder) {
        if (isFolder) {
            // Xóa tất cả blob trong folder
            Page<Blob> blobs = storage.list(
                    BUCKET_NAME,
                    Storage.BlobListOption.prefix(path.endsWith("/") ? path : path + "/")
            );
            boolean success = true;
            for (Blob blob : blobs.iterateAll()) {
                boolean deleted = storage.delete(blob.getBlobId());
                if (!deleted) success = false;
            }
            return success;
        } else {
            BlobId blobId = BlobId.of(BUCKET_NAME, path);
            return storage.delete(blobId);
        }
    }

    // ✅ Đổi tên file
    public boolean renameFile(String oldPath, String newPath) {
        BlobId oldBlobId = BlobId.of(BUCKET_NAME, oldPath);
        Blob oldBlob = storage.get(oldBlobId);

        if (oldBlob == null || !oldBlob.exists()) {
            return false;
        }

        // Copy sang tên mới
        BlobId newBlobId = BlobId.of(BUCKET_NAME, newPath);
        Storage.CopyRequest copyRequest = Storage.CopyRequest.newBuilder()
                .setSource(oldBlobId)
                .setTarget(BlobInfo.newBuilder(newBlobId).setContentType(oldBlob.getContentType()).build())
                .build();
        storage.copy(copyRequest);

        // Xóa file cũ
        storage.delete(oldBlobId);
        return true;
    }

    // ✅ Đổi tên folder
    public boolean renameFolder(String oldFolderPath, String newFolderPath) {
        if (!oldFolderPath.endsWith("/")) {
            oldFolderPath += "/";
        }
        if (!newFolderPath.endsWith("/")) {
            newFolderPath += "/";
        }

        Page<Blob> blobs = storage.list(
                BUCKET_NAME,
                Storage.BlobListOption.prefix(oldFolderPath)
        );

        boolean found = false;
        for (Blob blob : blobs.iterateAll()) {
            found = true;
            String oldName = blob.getName();
            String newName = oldName.replaceFirst(oldFolderPath, newFolderPath);

            // Copy sang tên mới
            BlobId newBlobId = BlobId.of(BUCKET_NAME, newName);
            Storage.CopyRequest copyRequest = Storage.CopyRequest.newBuilder()
                    .setSource(blob.getBlobId())
                    .setTarget(BlobInfo.newBuilder(newBlobId).setContentType(blob.getContentType()).build())
                    .build();
            storage.copy(copyRequest);

            // Xóa object cũ
            storage.delete(blob.getBlobId());
        }

        return found;
    }

    // ✅ Di chuyển file sang folder mới
    public boolean moveFileToFolder(String sourcePath, String targetFolderPath) {
        // Làm sạch đường dẫn
        String cleanSourcePath = StringUtils.cleanPath(sourcePath);
        String cleanTargetFolder = StringUtils.cleanPath(targetFolderPath);

        if (!cleanTargetFolder.endsWith("/")) {
            cleanTargetFolder += "/";
        }

        // Lấy file gốc
        BlobId sourceBlobId = BlobId.of(BUCKET_NAME, cleanSourcePath);
        Blob sourceBlob = storage.get(sourceBlobId);

        if (sourceBlob == null || !sourceBlob.exists()) {
            return false;
        }

        // Tách tên file ra (chỉ lấy phần cuối cùng, bỏ folder cũ)
        String filename = Paths.get(cleanSourcePath).getFileName().toString();

        // Ghép path mới
        String targetPath = cleanTargetFolder + filename;

        BlobId targetBlobId = BlobId.of(BUCKET_NAME, targetPath);

        // Copy sang folder mới
        Storage.CopyRequest copyRequest = Storage.CopyRequest.newBuilder()
                .setSource(sourceBlobId)
                .setTarget(BlobInfo.newBuilder(targetBlobId).setContentType(sourceBlob.getContentType()).build())
                .build();
        storage.copy(copyRequest);

        // Xóa file cũ
        storage.delete(sourceBlobId);

        return true;
    }

}