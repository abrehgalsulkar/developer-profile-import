package com.devprofiles.developerprofileimport.importer.async;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ImportJobRegistry {

    private final Map<String, ImportJob> jobs = new ConcurrentHashMap<>();

    public void register(ImportJob job) {
        jobs.put(job.getId(), job);
    }

    public Optional<ImportJob> findById(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public Collection<ImportJob> allJobs() {
        return jobs.values();
    }

    public void remove(String jobId) {
        jobs.remove(jobId);
    }

    public int count() {
        return jobs.size();
    }
}