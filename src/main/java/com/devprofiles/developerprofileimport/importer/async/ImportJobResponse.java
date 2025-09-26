package com.devprofiles.developerprofileimport.importer.async;

public record ImportJobResponse(String jobId, ImportJobStatus status, String statusUrl) {
}