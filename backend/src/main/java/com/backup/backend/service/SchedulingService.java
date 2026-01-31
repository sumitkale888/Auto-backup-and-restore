package com.backup.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {

    @Autowired
    private BackupService backupService;

    // Cron for 2 AM every day: "0 0 2 * * ?"
    // For testing, run every minute: "0 * * * * ?"
    @Scheduled(cron = "0 * * * * ?") 
    public void scheduledBackup() {
        System.out.println("Scheduler triggered backup...");
        backupService.performBackup();
    }
}