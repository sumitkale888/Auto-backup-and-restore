package com.backup.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class StorageConfig {

    @Value("${app.backup.source-directory}")
    private String sourcePath;

    @Value("${app.backup.destination-directory}")
    private String destPath;

    @PostConstruct
    public void init() {
        // Validate Source
        File source = new File(sourcePath);
        if (!source.exists()) {
            System.err.println("WARNING: Source directory does not exist: " + sourcePath);
        }

        // Validate Destination (Create if missing)
        File dest = new File(destPath);
        if (!dest.exists()) {
            boolean created = dest.mkdirs();
            if (created) {
                System.out.println("Created backup directory: " + destPath);
            }
        }
    }
}