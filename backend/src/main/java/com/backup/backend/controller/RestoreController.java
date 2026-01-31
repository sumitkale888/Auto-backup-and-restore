package com.backup.backend.controller;

import com.backup.backend.service.RestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restore")
@CrossOrigin("*")
public class RestoreController {

    @Autowired
    private RestoreService restoreService;

    // Endpoint: POST /api/restore/{id}
    @PostMapping("/{id}")
    public ResponseEntity<String> restoreBackup(@PathVariable Long id) {
        try {
            restoreService.restoreBackup(id);
            return ResponseEntity.ok("Restore triggered successfully for Job ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Restore Failed: " + e.getMessage());
        }
    }
}