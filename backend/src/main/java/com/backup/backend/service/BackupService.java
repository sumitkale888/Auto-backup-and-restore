package com.backup.backend.service;

import com.backup.backend.entity.*;
import com.backup.backend.repository.*;
import com.backup.backend.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
@Service
public class BackupService {

    @Value("${app.backup.source-directory}")
    private String sourcePath;

    @Value("${app.backup.destination-directory}")
    private String destPath;

    @Autowired private BackupJobRepository jobRepository;
    @Autowired private ChecksumUtil checksumUtil;
    @Autowired private CompressionUtil compressionUtil;
    @Autowired private FileIOUtil fileIOUtil;

    @Transactional
    public void performBackup() {
        BackupJob job = new BackupJob();
        job.setTimestamp(LocalDateTime.now());
        job.setStatus(BackupJob.BackupStatus.FAILED); // Default to fail until success
        
        try {
            File sourceDir = new File(sourcePath);
            List<File> allFiles = fileIOUtil.listAllFiles(sourceDir);
            
            // 1. Get Previous Backup Data for Comparison
            Optional<BackupJob> lastJobOpt = jobRepository.findFirstByStatusOrderByTimestampDesc(BackupJob.BackupStatus.SUCCESS);
            
            List<File> filesToBackup = new ArrayList<>();
            List<FileMetadata> newMetadataList = new ArrayList<>();
            boolean isIncremental = lastJobOpt.isPresent();

            // 2. Map Previous Hashes for fast lookup
            Map<String, String> previousFileHashes = new HashMap<>();
            if (isIncremental) {
                for (FileMetadata fm : lastJobOpt.get().getFileMetadataList()) {
                    previousFileHashes.put(fm.getFilePath(), fm.getFileHash());
                }
            }

            // 3. Scan & Compare (The Incremental Logic)
            for (File file : allFiles) {
                String relativePath = sourceDir.toURI().relativize(file.toURI()).getPath();
                String currentHash = checksumUtil.calculateMD5(file);

                // Add to current metadata tracking
                FileMetadata metadata = new FileMetadata();
                metadata.setFilePath(relativePath);
                metadata.setFileHash(currentHash);
                metadata.setSize(file.length());
                metadata.setBackupJob(job);
                newMetadataList.add(metadata);

                // DECISION: Do we save this file?
                if (!isIncremental) {
                    // Full Backup -> Save Everything
                    filesToBackup.add(file);
                } else {
                    // Incremental -> Save only if hash changed or file is new
                    String oldHash = previousFileHashes.get(relativePath);
                    if (oldHash == null || !oldHash.equals(currentHash)) {
                        filesToBackup.add(file);
                    }
                }
            }
            
            job.setType(isIncremental && !filesToBackup.isEmpty() ? BackupJob.BackupType.INCREMENTAL : BackupJob.BackupType.FULL);
            
            // 4. Compress
            if (filesToBackup.isEmpty()) {
                System.out.println("No changes detected. Skipping backup.");
                return; // Nothing to do
            }

            String zipFileName = "backup_" + job.getType() + "_" + System.currentTimeMillis() + ".zip";
            String fullZipPath = destPath + File.separator + zipFileName;
            
            new File(destPath).mkdirs(); // Ensure dest exists
            compressionUtil.zipFiles(filesToBackup, sourceDir, fullZipPath);

            // 5. Save Record
            job.setStoredPath(fullZipPath);
            job.setFileMetadataList(newMetadataList);
            job.setStatus(BackupJob.BackupStatus.SUCCESS);
            job.setTotalSize(new File(fullZipPath).length());
            
            jobRepository.save(job);
            System.out.println("Backup Success: " + zipFileName);

        } catch (Exception e) {
            e.printStackTrace();
            jobRepository.save(job); // Save as FAILED
        }
    }
}