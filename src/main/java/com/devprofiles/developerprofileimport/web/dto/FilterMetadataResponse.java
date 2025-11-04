package com.devprofiles.developerprofileimport.web.dto;

import java.util.List;

public record FilterMetadataResponse(
        List<FilterOptionDto> technologies,
        List<String> experienceRanges,
        List<String> projectCompletionRanges,
        List<FilterOptionDto> workLocations,
        List<FilterOptionDto> availabilities,
        List<FilterOptionDto> languages,
        List<FilterOptionDto> languageProficiencies) {
}
