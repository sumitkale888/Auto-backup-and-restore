package com.backup.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "backup_jobs")
public class BackupJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private BackupStatus status; 

    @Enumerated(EnumType.STRING)
    private BackupType type; // FULL or INCREMENTAL

    private String storedPath; // Location of the ZIP file
    private Long totalSize;    // Size in bytes

    @OneToMany(mappedBy = "backupJob", cascade = CascadeType.ALL)
    private List<FileMetadata> fileMetadataList;

    public enum BackupStatus { SUCCESS, FAILED }
    public enum BackupType { FULL, INCREMENTAL }
}