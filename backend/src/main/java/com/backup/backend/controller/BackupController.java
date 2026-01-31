package com.backup.backend.controller;

import com.backup.backend.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin("*")
public class BackupController {

    @Autowired
    private BackupService backupService;

    // Endpoint: POST /api/backup/trigger
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerManualBackup() {
        // Run in a separate thread so the API returns immediately
        new Thread(() -> backupService.performBackup()).start();
        return ResponseEntity.ok("Manual backup started in background.");
    }
}