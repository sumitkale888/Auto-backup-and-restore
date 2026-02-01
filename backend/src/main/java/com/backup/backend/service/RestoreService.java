package com.backup.backend.service;

import com.backup.backend.entity.BackupJob;
import com.backup.backend.repository.BackupJobRepository;
import com.backup.backend.utils.CompressionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException; // Import standard IO Exception
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestoreService {

    @Autowired private BackupJobRepository jobRepository;
    @Autowired private CompressionUtil compressionUtil;

    @Value("${app.backup.source-directory}")
    private String sourcePath; 

    public void restoreBackup(Long jobId) throws Exception {
        // 1. Get the requested backup
        Optional<BackupJob> targetJobOpt = jobRepository.findById(jobId);
        if (targetJobOpt.isEmpty()) {
            throw new Exception("Backup ID not found");
        }
        BackupJob targetJob = targetJobOpt.get();
        if (targetJob.getStatus() == BackupJob.BackupStatus.FAILED) {
            throw new Exception("Cannot restore a failed backup");
        }

        List<BackupJob> restoreChain = new ArrayList<>();

        // 2. Build the Chain
        if (targetJob.getType() == BackupJob.BackupType.FULL) {
            // If user selected a FULL backup, we just restore that one
            restoreChain.add(targetJob);
        } else {
            // If Incremental, we must find the base FULL backup
            Optional<BackupJob> baseFullOpt = jobRepository.findBaseFullBackup(targetJob.getTimestamp());
            if (baseFullOpt.isEmpty()) {
                throw new Exception("Broken Chain: Could not find a Base FULL backup for this incremental point.");
            }
            BackupJob baseFull = baseFullOpt.get();

            // Add the Base Full
            restoreChain.add(baseFull);

            // Add all Incrementals between Base and Target
            List<BackupJob> intermediates = jobRepository.findIncrementalsBetween(baseFull.getTimestamp(), targetJob.getTimestamp());
            restoreChain.addAll(intermediates);
        }

        // 3. Execute Restore (Loop through the chain)
        System.out.println("Starting Chain Restore for Job ID: " + jobId);
        
        // Clean source directory before starting (Optional: Safety Step)
        // cleanDirectory(new File(sourcePath)); 

        for (BackupJob job : restoreChain) {
            System.out.println(" -> Restoring part: " + job.getType() + " (ID: " + job.getId() + ")");
            try {
                compressionUtil.unzip(job.getStoredPath(), sourcePath);
            } catch (IOException e) {
                throw new Exception("Failed to unzip file: " + job.getStoredPath());
            }
        }
        
        System.out.println("Restore Chain Completed Successfully!");
    }
}