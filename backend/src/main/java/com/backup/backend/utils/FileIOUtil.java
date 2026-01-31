package com.backup.backend.utils;

import org.springframework.stereotype.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileIOUtil {
    
    // Recursively list all files in a directory
    public List<File> listAllFiles(File dir) {
        List<File> files = new ArrayList<>();
        if (dir == null || !dir.exists()) return files;
        
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isFile()) {
                    files.add(entry);
                } else if (entry.isDirectory()) {
                    files.addAll(listAllFiles(entry));
                }
            }
        }
        return files;
    }
}