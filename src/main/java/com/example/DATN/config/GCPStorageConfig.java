package com.example.DATN.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GCPStorageConfig {

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setProjectId("gen-lang-client-0154921035")
                .setCredentials(
                        GoogleCredentials.fromStream(
                                new FileInputStream("E:/DATNProject/DATN/gen-lang-client-0154921035-c044fd2d1fb5.json")
                        )
                )
                .build()
                .getService();
    }
}
