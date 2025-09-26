package com.devprofiles.developerprofileimport.importer.async;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import com.devprofiles.developerprofileimport.importer.service.DeveloperExcelImportService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DeveloperImportJobService {

    private static final Logger log = LoggerFactory.getLogger(DeveloperImportJobService.class);

    private final ThreadPoolTaskExecutor taskExecutor;
    private final ImportJobRegistry jobRegistry;
    private final ImportFileStorage fileStorage;
    private final DeveloperExcelImportService importService;

    public DeveloperImportJobService(ThreadPoolTaskExecutor taskExecutor, ImportJobRegistry jobRegistry,
            ImportFileStorage fileStorage, DeveloperExcelImportService importService) {
        this.taskExecutor = taskExecutor;
        this.jobRegistry = jobRegistry;
        this.fileStorage = fileStorage;
        this.importService = importService;
    }

    public ImportJob submitJob(MultipartFile workbook, MultipartFile assets) throws IOException {
        if (workbook == null || workbook.isEmpty()) {
            throw new IllegalArgumentException("Workbook file must be provided");
        }

        String jobId = UUID.randomUUID().toString();
        Path jobDir = fileStorage.createJobDirectory(jobId);
        Path workbookPath = fileStorage.saveWorkbook(jobDir, workbook);
        Path assetsPath = null;
        String assetsFilename = null;
        if (assets != null && !assets.isEmpty()) {
            assetsPath = fileStorage.saveAssets(jobDir, assets);
            assetsFilename = assets.getOriginalFilename();
        }

        ImportJob job = new ImportJob(jobId, workbook.getOriginalFilename(), assetsFilename,
                jobDir, workbookPath, assetsPath);
        jobRegistry.register(job);

        try {
            taskExecutor.submit(() -> runJob(job));
        } catch (TaskRejectedException ex) {
            jobRegistry.remove(jobId);
            fileStorage.deleteRecursively(jobDir);
            throw ex;
        }

        return job;
    }

    public Optional<ImportJobStatusResponse> getJobStatus(String jobId) {
        return jobRegistry.findById(jobId).map(this::toResponse);
    }

    private ImportJobStatusResponse toResponse(ImportJob job) {
        return new ImportJobStatusResponse(
                job.getId(),
                job.getStatus(),
                job.getWorkbookFilename(),
                job.getAssetsFilename().orElse(null),
                job.getSubmittedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getReport().orElse(null),
                job.getErrorMessage().orElse(null));
    }

    private void runJob(ImportJob job) {
        job.markRunning();
        try {
            var workbookFile = new PathMultipartFile(job.getWorkbookPath(), job.getWorkbookFilename());
            MultipartFile assetsFile = job.getAssetsPath()
                    .map(path -> new PathMultipartFile(path, job.getAssetsFilename().orElse(null)))
                    .orElse(null);

            DeveloperProfileImportReport report = importService.importExcel(workbookFile, assetsFile);
            job.markSucceeded(report);
        } catch (Exception ex) {
            log.error("Import job {} failed", job.getId(), ex);
            String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            job.markFailed(message);
        } finally {
            fileStorage.deleteRecursively(job.getJobDirectory());
        }
    }
}