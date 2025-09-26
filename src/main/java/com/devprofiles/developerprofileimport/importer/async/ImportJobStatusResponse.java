package com.devprofiles.developerprofileimport.importer.async;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import java.time.Instant;

public record ImportJobStatusResponse(
        String jobId,
        ImportJobStatus status,
        String workbookFilename,
        String assetsFilename,
        Instant submittedAt,
        Instant startedAt,
        Instant completedAt,
        DeveloperProfileImportReport report,
        String errorMessage) {
}