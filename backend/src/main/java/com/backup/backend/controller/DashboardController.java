package com.backup.backend.controller;

import com.backup.backend.entity.BackupJob;
import com.backup.backend.repository.BackupJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private BackupJobRepository jobRepository;

    // Endpoint: GET /api/dashboard/history
    @GetMapping("/history")
    public List<BackupJob> getBackupHistory() {
        // Returns list of all backups (Success/Fail) sorted by ID
        return jobRepository.findAll();
    }

    // Endpoint: GET /api/dashboard/stats
    @GetMapping("/stats")
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalJobs = jobRepository.count();
        long successJobs = jobRepository.findAll().stream()
                .filter(job -> job.getStatus() == BackupJob.BackupStatus.SUCCESS)
                .count();

        stats.put("totalBackups", totalJobs);
        stats.put("successRate", totalJobs > 0 ? (successJobs * 100 / totalJobs) + "%" : "0%");
        stats.put("lastBackupTime", "Check History"); // You can implement logic to get max date
        
        return stats;
    }
}