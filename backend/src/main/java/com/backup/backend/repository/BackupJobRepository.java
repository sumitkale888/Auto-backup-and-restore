package com.backup.backend.repository;

import com.backup.backend.entity.BackupJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupJobRepository extends JpaRepository<BackupJob, Long> {
    // Find the latest successful backup to compare files against
    Optional<BackupJob> findFirstByStatusOrderByTimestampDesc(BackupJob.BackupStatus status);
// 1. Find the latest FULL backup that happened BEFORE (or at) a specific time
    @Query("SELECT b FROM BackupJob b WHERE b.type = 'FULL' AND b.status = 'SUCCESS' AND b.timestamp <= :targetTime ORDER BY b.timestamp DESC LIMIT 1")
    Optional<BackupJob> findBaseFullBackup(@Param("targetTime") LocalDateTime targetTime);

    // 2. Find all INCREMENTAL backups between two times
    @Query("SELECT b FROM BackupJob b WHERE b.type = 'INCREMENTAL' AND b.status = 'SUCCESS' AND b.timestamp > :startTime AND b.timestamp <= :endTime ORDER BY b.timestamp ASC")
    List<BackupJob> findIncrementalsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}