package com.devprofiles.developerprofileimport.service.query;

import java.util.List;
import java.util.Optional;

public record DeveloperSearchCriteria(
        String keyword,
        List<String> technologies,
        List<RangeFilter> experienceRanges,
        List<RangeFilter> projectCompletionRanges,
        List<String> workLocations,
        List<String> availabilities,
        List<String> languages,
        String languageProficiency,
        Double hourlyRateMin,
        Double hourlyRateMax) {

    public Optional<String> keywordOptional() {
        return Optional.ofNullable(keyword).filter(s -> !s.isBlank());
    }

    public Optional<String> languageProficiencyOptional() {
        return Optional.ofNullable(languageProficiency).filter(s -> !s.isBlank());
    }

    public Optional<Double> hourlyRateMinOptional() {
        return Optional.ofNullable(hourlyRateMin);
    }

    public Optional<Double> hourlyRateMaxOptional() {
        return Optional.ofNullable(hourlyRateMax);
    }
}
