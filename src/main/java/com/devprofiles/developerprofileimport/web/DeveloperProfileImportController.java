package com.devprofiles.developerprofileimport.web;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import com.devprofiles.developerprofileimport.importer.service.DeveloperProfileImportService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/developer-profiles")
@RequiredArgsConstructor
public class DeveloperProfileImportController {

    private final DeveloperProfileImportService importService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DeveloperProfileImportReport importDeveloperProfiles(@RequestPart("file") MultipartFile file)
        throws IOException {
        return importService.importFile(file);
    }
}
