package com.devprofiles.developerprofileimport.importer.async;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

public class ImportJob {

    private final String id;
    private final String workbookFilename;
    private final String assetsFilename;
    private final Path jobDirectory;
    private final Path workbookPath;
    private final Path assetsPath;
    private final Instant submittedAt;

    private volatile Instant startedAt;
    private volatile Instant completedAt;
    private volatile ImportJobStatus status;
    private volatile DeveloperProfileImportReport report;
    private volatile String errorMessage;

    public ImportJob(String id, String workbookFilename, String assetsFilename,
            Path jobDirectory, Path workbookPath, Path assetsPath) {
        this.id = id;
        this.workbookFilename = workbookFilename;
        this.assetsFilename = assetsFilename;
        this.jobDirectory = jobDirectory;
        this.workbookPath = workbookPath;
        this.assetsPath = assetsPath;
        this.submittedAt = Instant.now();
        this.status = ImportJobStatus.QUEUED;
    }

    public String getId() {
        return id;
    }

    public String getWorkbookFilename() {
        return workbookFilename;
    }

    public Optional<String> getAssetsFilename() {
        return Optional.ofNullable(assetsFilename);
    }

    public Path getJobDirectory() {
        return jobDirectory;
    }

    public Path getWorkbookPath() {
        return workbookPath;
    }

    public Optional<Path> getAssetsPath() {
        return Optional.ofNullable(assetsPath);
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public ImportJobStatus getStatus() {
        return status;
    }

    public Optional<DeveloperProfileImportReport> getReport() {
        return Optional.ofNullable(report);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public synchronized void markRunning() {
        this.status = ImportJobStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public synchronized void markSucceeded(DeveloperProfileImportReport report) {
        this.status = ImportJobStatus.SUCCEEDED;
        this.report = report;
        this.completedAt = Instant.now();
    }

    public synchronized void markFailed(String errorMessage) {
        this.status = ImportJobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }
}