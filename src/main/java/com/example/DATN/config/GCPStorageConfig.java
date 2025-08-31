package com.example.DATN.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GCPStorageConfig {

    @Value("${gcp.credentials.location}")
    private Resource gcpKeyFile;
    @Value("${gcp.project.id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setProjectId("gen-lang-client-0154921035")
                .setCredentials(GoogleCredentials.fromStream(gcpKeyFile.getInputStream()))
                .build()
                .getService();
    }

//    @Bean
//    public Storage storage() throws IOException {
//        // Khi chạy trên Cloud Run, GoogleCredentials.getApplicationDefault() sẽ tự động được sử dụng
//        // Bạn không cần cung cấp file key nữa.
//        return StorageOptions.newBuilder()
//                .setProjectId(projectId)
//                .build()
//                .getService();
//    }

}
