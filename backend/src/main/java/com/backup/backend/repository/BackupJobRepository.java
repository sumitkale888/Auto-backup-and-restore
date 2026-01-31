package com.backup.backend.repository;

import com.backup.backend.entity.BackupJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BackupJobRepository extends JpaRepository<BackupJob, Long> {
    // Find the latest successful backup to compare files against
    Optional<BackupJob> findFirstByStatusOrderByTimestampDesc(BackupJob.BackupStatus status);
}