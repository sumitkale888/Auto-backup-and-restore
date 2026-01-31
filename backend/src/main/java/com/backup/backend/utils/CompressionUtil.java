package com.backup.backend.utils;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class CompressionUtil {

    // Zips a list of specific files into a destination zip
    public void zipFiles(List<File> filesToZip, File sourceRoot, String destZipPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (File file : filesToZip) {
                if (file.isDirectory()) continue;

                // Create relative path for zip entry (e.g., "docs/report.txt")
                String relativePath = sourceRoot.toURI().relativize(file.toURI()).getPath();
                
                ZipEntry zipEntry = new ZipEntry(relativePath);
                zos.putNextEntry(zipEntry);

                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
    }
    
    // Unzips a file to a destination
    public void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }
}