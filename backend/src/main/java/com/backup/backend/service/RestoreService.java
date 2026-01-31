package com.backup.backend.service;

import com.backup.backend.entity.BackupJob;
import com.backup.backend.repository.BackupJobRepository;
import com.backup.backend.utils.CompressionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestoreService {

    @Autowired private BackupJobRepository jobRepository;
    @Autowired private CompressionUtil compressionUtil;

    @Value("${app.backup.source-directory}")
    private String sourcePath; // We restore BACK to the source (Danger! Overwrites data)

    public void restoreBackup(Long jobId) throws Exception {
        Optional<BackupJob> jobOpt = jobRepository.findById(jobId);
        
        if (jobOpt.isEmpty()) {
            throw new Exception("Backup ID not found");
        }

        BackupJob job = jobOpt.get();
        if (job.getStatus() == BackupJob.BackupStatus.FAILED) {
            throw new Exception("Cannot restore a failed backup");
        }

        // Logic: Unzip the stored file back to the source directory
        // In a real 'incremental chain' restore, you would loop from Full -> Inc 1 -> Inc 2 -> Target
        // For MVP, we unzip the specific point requested.
        
        System.out.println("Restoring from: " + job.getStoredPath());
        compressionUtil.unzip(job.getStoredPath(), sourcePath);
        System.out.println("Restore Completed!");
    }
}