package com.devprofiles.developerprofileimport.service;

import com.devprofiles.developerprofileimport.repository.HdAvailabilitiesRepository;
import com.devprofiles.developerprofileimport.repository.HdExperienceRangesRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguageProficiencyRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguagesRepository;
import com.devprofiles.developerprofileimport.repository.HdProjectCompletionRangeRepository;
import com.devprofiles.developerprofileimport.repository.HdTechnologiesRepository;
import com.devprofiles.developerprofileimport.repository.HdWorkLocationsRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FilterOptionService {

  private final HdTechnologiesRepository technologiesRepository;
  private final HdExperienceRangesRepository experienceRangesRepository;
  private final HdProjectCompletionRangeRepository projectCompletionRangeRepository;
  private final HdWorkLocationsRepository workLocationsRepository;
  private final HdAvailabilitiesRepository availabilitiesRepository;
  private final HdLanguagesRepository languagesRepository;
  private final HdLanguageProficiencyRepository languageProficiencyRepository;

  public FilterOptionService(HdTechnologiesRepository technologiesRepository,
                             HdExperienceRangesRepository experienceRangesRepository,
                             HdProjectCompletionRangeRepository projectCompletionRangeRepository,
                             HdWorkLocationsRepository workLocationsRepository,
                             HdAvailabilitiesRepository availabilitiesRepository,
                             HdLanguagesRepository languagesRepository,
                             HdLanguageProficiencyRepository languageProficiencyRepository) {
    this.technologiesRepository = technologiesRepository;
    this.experienceRangesRepository = experienceRangesRepository;
    this.projectCompletionRangeRepository = projectCompletionRangeRepository;
    this.workLocationsRepository = workLocationsRepository;
    this.availabilitiesRepository = availabilitiesRepository;
    this.languagesRepository = languagesRepository;
    this.languageProficiencyRepository = languageProficiencyRepository;
  }

  public FilterOptions loadFilterOptions() {
    return new FilterOptions(
        mapLabels(
            technologiesRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByTechnologyAsc(),
            tech -> tech.getTechnology()
        ),
        mapLabels(
            experienceRangesRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByMinimumExperienceAsc(),
            range -> range.getExperienceRangeLabel()
        ),
        mapLabels(
            projectCompletionRangeRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByMinProjectsAsc(),
            range -> range.getProjectCompletionRangeLabel()
        ),
        mapLabels(
            workLocationsRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByWorkLocationLabelAsc(),
            location -> location.getWorkLocationLabel()
        ),
        mapLabels(
            availabilitiesRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByAvailabilityLabelAsc(),
            availability -> availability.getAvailabilityLabel()
        ),
        mapLabels(
            languagesRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByNameAsc(),
            language -> language.getName()
        ),
        mapLabels(
            languageProficiencyRepository
                .findAllByIsActiveTrueAndIsDeletedFalseOrderByProficiencyLabelAsc(),
            proficiency -> proficiency.getProficiencyLabel()
        )
    );
  }

  private <T> List<String> mapLabels(List<T> source, Function<T, String> extractor) {
    if (source == null || source.isEmpty()) {
      return List.of();
    }
    return source.stream()
        .map(extractor)
        .map(this::normalize)
        .filter(StringUtils::hasText)
        .distinct()
        .collect(Collectors.toList());
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim();
  }

  public record FilterOptions(
      List<String> technologies,
      List<String> experienceRanges,
      List<String> projectCompletions,
      List<String> workLocations,
      List<String> availabilities,
      List<String> languages,
      List<String> languageProficiencies
  ) {
  }
}

