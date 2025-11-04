package com.devprofiles.developerprofileimport.web.dto;

import java.util.List;

public record DeveloperProfileDto(
        Long id,
        String firstName,
        String lastName,
        String designation,
        boolean verified,
        Double hourlyRate,
        Long experienceYears,
        Long totalProjectCompletion,
        Long totalWorkedHours,
        Double averageRating,
        Long totalReview,
        String jobTitle,
        String profilePictureUrl,
        String introductionVideoUrl,
        String resumeUrl,
        String about,
        String permanentAddress,
        String temporaryAddress,
        List<String> availabilities,
        List<String> workLocations,
        List<String> technologies,
        List<LanguageSkillDto> languages) {
}
