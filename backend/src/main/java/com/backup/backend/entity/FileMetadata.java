package com.backup.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;   // Relative path (e.g., "reports/2024.pdf")
    private String fileHash;   // MD5 Checksum
    private long size;

    @ManyToOne
    @JoinColumn(name = "backup_job_id")
    @JsonIgnore
    private BackupJob backupJob;
}