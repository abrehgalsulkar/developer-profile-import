package com.devprofiles.developerprofileimport.service.metadata;

import com.devprofiles.developerprofileimport.domain.HdAvailabilities;
import com.devprofiles.developerprofileimport.domain.HdLanguages;
import com.devprofiles.developerprofileimport.domain.HdLanguageProficiency;
import com.devprofiles.developerprofileimport.domain.HdTechnologies;
import com.devprofiles.developerprofileimport.domain.HdWorkLocations;
import com.devprofiles.developerprofileimport.repository.HdAvailabilitiesRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguagesRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguageProficiencyRepository;
import com.devprofiles.developerprofileimport.repository.HdTechnologiesRepository;
import com.devprofiles.developerprofileimport.repository.HdWorkLocationsRepository;
import com.devprofiles.developerprofileimport.web.dto.FilterMetadataResponse;
import com.devprofiles.developerprofileimport.web.dto.FilterOptionDto;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class FilterMetadataService {

    private static final List<String> EXPERIENCE_RANGES = List.of("0-5", "6-10", "11-15", "16-20", "21+");

    private final HdTechnologiesRepository technologiesRepository;
    private final HdAvailabilitiesRepository availabilitiesRepository;
    private final HdWorkLocationsRepository workLocationsRepository;
    private final HdLanguagesRepository languagesRepository;
    private final HdLanguageProficiencyRepository languageProficiencyRepository;

    public FilterMetadataService(HdTechnologiesRepository technologiesRepository,
                                 HdAvailabilitiesRepository availabilitiesRepository,
                                 HdWorkLocationsRepository workLocationsRepository,
                                 HdLanguagesRepository languagesRepository,
                                 HdLanguageProficiencyRepository languageProficiencyRepository) {
        this.technologiesRepository = technologiesRepository;
        this.availabilitiesRepository = availabilitiesRepository;
        this.workLocationsRepository = workLocationsRepository;
        this.languagesRepository = languagesRepository;
        this.languageProficiencyRepository = languageProficiencyRepository;
    }

    public FilterMetadataResponse getMetadata() {
        List<FilterOptionDto> technologies = technologiesRepository.findAll().stream()
                .filter(this::isActive)
                .map(entity -> toOption(entity.getTechnology()))
                .toList();

        List<FilterOptionDto> workLocations = workLocationsRepository.findAll().stream()
                .filter(this::isActive)
                .map(entity -> toOption(entity.getWorkLocationLabel()))
                .toList();

        List<FilterOptionDto> availabilities = availabilitiesRepository.findAll().stream()
                .filter(this::isActive)
                .map(entity -> toOption(entity.getAvailabilityLabel()))
                .toList();

        List<FilterOptionDto> languages = languagesRepository.findAll().stream()
                .filter(this::isActive)
                .map(entity -> toOption(entity.getName()))
                .toList();

        List<FilterOptionDto> languageProficiencies = languageProficiencyRepository.findAll().stream()
                .filter(this::isActive)
                .map(entity -> toOption(entity.getProficiencyLabel()))
                .toList();

        return new FilterMetadataResponse(
                technologies,
                EXPERIENCE_RANGES,
                EXPERIENCE_RANGES,
                workLocations,
                availabilities,
                languages,
                languageProficiencies);
    }

    private FilterOptionDto toOption(String label) {
        if (label == null) {
            return new FilterOptionDto("", "");
        }
        String trimmed = label.trim();
        String value = trimmed.toLowerCase(Locale.ENGLISH);
        return new FilterOptionDto(trimmed, value);
    }

    private boolean isActive(HdTechnologies entity) {
        return !Boolean.TRUE.equals(entity.getIsDeleted()) && !isFalse(entity.getIsActive());
    }

    private boolean isActive(HdAvailabilities entity) {
        return !Boolean.TRUE.equals(entity.getIsDeleted()) && !isFalse(entity.getIsActive());
    }

    private boolean isActive(HdWorkLocations entity) {
        return !Boolean.TRUE.equals(entity.getIsDeleted()) && !isFalse(entity.getIsActive());
    }

    private boolean isActive(HdLanguages entity) {
        return !Boolean.TRUE.equals(entity.getIsDeleted()) && !isFalse(entity.getIsActive());
    }

    private boolean isActive(HdLanguageProficiency entity) {
        return !Boolean.TRUE.equals(entity.getIsDeleted()) && !isFalse(entity.getIsActive());
    }

    private boolean isFalse(Boolean value) {
        return value != null && Boolean.FALSE.equals(value);
    }
}
