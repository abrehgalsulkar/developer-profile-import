package com.devprofiles.developerprofileimport.web;

import com.devprofiles.developerprofileimport.importer.async.DeveloperImportJobService;
import com.devprofiles.developerprofileimport.importer.async.ImportJob;
import com.devprofiles.developerprofileimport.importer.async.ImportJobResponse;
import com.devprofiles.developerprofileimport.importer.async.ImportJobStatusResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/developer-profiles")
@RequiredArgsConstructor
public class DeveloperProfileExcelImportController {

    private final DeveloperImportJobService jobService;

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<ImportJobResponse> importDeveloperProfiles(
            @RequestPart("workbook") MultipartFile workbook,
            @RequestPart(value = "assets", required = false) MultipartFile assets) throws IOException {
        try {
            ImportJob job = jobService.submitJob(workbook, assets);
            var statusUri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{jobId}")
                    .buildAndExpand(job.getId())
                    .toUri();
            ImportJobResponse response = new ImportJobResponse(job.getId(), job.getStatus(), statusUri.toString());
            return ResponseEntity.accepted()
                    .location(statusUri)
                    .body(response);
        } catch (TaskRejectedException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Import queue is full", ex);
        }
    }

    @GetMapping("/import/{jobId}")
    public ResponseEntity<ImportJobStatusResponse> getImportJobStatus(@PathVariable String jobId) {
        return jobService.getJobStatus(jobId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Import job not found"));
    }
}