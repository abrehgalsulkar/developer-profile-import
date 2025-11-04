package com.devprofiles.developerprofileimport.web.dto;

import java.util.List;

public record DeveloperListResponse(
        List<DeveloperProfileDto> items,
        long total,
        int page,
        int size) {
}
