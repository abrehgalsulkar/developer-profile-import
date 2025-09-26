package com.devprofiles.developerprofileimport.importer.async;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImportFileStorage {

    private final Path baseDir = Paths.get("storage", "tmp");

    public ImportFileStorage() {
        try {
            Files.createDirectories(baseDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create import temp directory", ex);
        }
    }

    public Path createJobDirectory(String jobId) throws IOException {
        Path jobDir = baseDir.resolve(jobId);
        Files.createDirectories(jobDir);
        return jobDir;
    }

    public Path saveWorkbook(Path jobDir, MultipartFile workbook) throws IOException {
        String filename = workbook.getOriginalFilename();
        Path target = jobDir.resolve(filename == null ? "workbook.xlsx" : filename);
        copy(workbook, target);
        return target;
    }

    public Path saveAssets(Path jobDir, MultipartFile assets) throws IOException {
        String filename = assets.getOriginalFilename();
        Path target = jobDir.resolve(filename == null ? "assets.zip" : filename);
        copy(assets, target);
        return target;
    }

    public void deleteRecursively(Path jobDir) {
        if (jobDir == null) {
            return;
        }
        try {
            if (Files.notExists(jobDir)) {
                return;
            }
            Files.walk(jobDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private void copy(MultipartFile file, Path target) throws IOException {
        try (var input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}