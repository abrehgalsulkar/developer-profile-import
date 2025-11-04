package com.devprofiles.developerprofileimport.service.query;

import com.devprofiles.developerprofileimport.domain.HdDeveloperKnownLanguages;
import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.domain.HdDesignations;
import com.devprofiles.developerprofileimport.domain.HdLanguages;
import com.devprofiles.developerprofileimport.domain.HdLanguageProficiency;
import com.devprofiles.developerprofileimport.repository.HdDeveloperKnownLanguagesRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProfileRepository;
import com.devprofiles.developerprofileimport.web.dto.DeveloperProfileDto;
import com.devprofiles.developerprofileimport.web.dto.LanguageSkillDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeveloperProfileQueryService {

    private final HdDeveloperProfileRepository profileRepository;
    private final HdDeveloperKnownLanguagesRepository knownLanguagesRepository;

    public DeveloperProfileQueryService(HdDeveloperProfileRepository profileRepository,
                                        HdDeveloperKnownLanguagesRepository knownLanguagesRepository) {
        this.profileRepository = profileRepository;
        this.knownLanguagesRepository = knownLanguagesRepository;
    }

    @Transactional(readOnly = true)
    public Page<DeveloperProfileDto> search(DeveloperSearchCriteria criteria, Pageable pageable) {
        Specification<HdDeveloperProfile> specification = buildSpecification(criteria);
        Page<HdDeveloperProfile> page = profileRepository.findAll(specification, pageable);

        List<Long> profileIds = page.getContent().stream()
                .map(HdDeveloperProfile::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, List<HdDeveloperKnownLanguages>> languagesByProfile = profileIds.isEmpty()
                ? Map.of()
                : knownLanguagesRepository.findByHdDeveloperProfileIdIn(profileIds).stream()
                .collect(Collectors.groupingBy(lang -> lang.getHdDeveloperProfile().getId()));

        List<DeveloperProfileDto> dtos = page.getContent().stream()
                .map(profile -> toDto(profile, languagesByProfile.getOrDefault(profile.getId(), List.of())))
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private DeveloperProfileDto toDto(HdDeveloperProfile profile,
                                      Collection<HdDeveloperKnownLanguages> languageRows) {
        List<String> availabilities = profile.getAvailabilities() == null
                ? List.of()
                : profile.getAvailabilities().stream()
                        .map(av -> av.getAvailabilityLabel())
                        .filter(Objects::nonNull)
                        .toList();

        List<String> workLocations = profile.getWorkLocations() == null
                ? List.of()
                : profile.getWorkLocations().stream()
                        .map(loc -> loc.getWorkLocationLabel())
                        .filter(Objects::nonNull)
                        .toList();

        List<String> technologies = profile.getOverallExperienceSkills() == null
                ? List.of()
                : profile.getOverallExperienceSkills().stream()
                        .map(tech -> tech.getTechnology())
                        .filter(Objects::nonNull)
                        .toList();

        List<LanguageSkillDto> languages = languageRows.stream()
                .sorted(Comparator.comparing(lang -> safeExtract(lang.getHdLanguages(), HdLanguages::getName, "")))
                .map(lang -> new LanguageSkillDto(
                        safeExtract(lang.getHdLanguages(), HdLanguages::getName, null),
                        safeExtract(lang.getHdLanguageProficiency(), HdLanguageProficiency::getProficiencyLabel, null)))
                .toList();

        return new DeveloperProfileDto(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                safeExtract(profile.getDesignation(), HdDesignations::getDesignation, null),
                Boolean.TRUE.equals(profile.getIsVerified()),
                profile.getHourlyRate(),
                profile.getNumberOfExperience(),
                profile.getTotalProjectCompletion(),
                profile.getTotalWorkedHours(),
                profile.getAverageRating(),
                profile.getTotalReview(),
                profile.getJobTitle(),
                profile.getProfilePictureUrl(),
                profile.getIntroductionVideoUrl(),
                profile.getResumeUrl(),
                profile.getAbout(),
                profile.getPermanentAddress(),
                profile.getTemporaryAddress(),
                availabilities,
                workLocations,
                technologies,
                languages);
    }

    private Specification<HdDeveloperProfile> buildSpecification(DeveloperSearchCriteria criteria) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(
                    cb.isNull(root.get("isDeleted")),
                    cb.isFalse(root.get("isDeleted"))
            ));

            criteria.keywordOptional().ifPresent(keyword -> {
                String pattern = "%" + keyword.toLowerCase(Locale.ENGLISH) + "%";
                var designationJoin = root.join("designation", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("jobTitle")), pattern),
                        cb.like(cb.lower(root.get("about")), pattern),
                        cb.like(cb.lower(designationJoin.get("designation")), pattern)
                ));
            });

            if (!criteria.technologies().isEmpty()) {
                var techJoin = root.join("overallExperienceSkills", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.lower(techJoin.get("technology")).in(criteria.technologies()));
            }

            if (!criteria.workLocations().isEmpty()) {
                var workJoin = root.join("workLocations", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.lower(workJoin.get("workLocationLabel")).in(criteria.workLocations()));
            }

            if (!criteria.availabilities().isEmpty()) {
                var availabilityJoin = root.join("availabilities", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.lower(availabilityJoin.get("availabilityLabel")).in(criteria.availabilities()));
            }

            if (!criteria.languages().isEmpty() || criteria.languageProficiencyOptional().isPresent()) {
                var languageJoin = root.join("knownLanguages", jakarta.persistence.criteria.JoinType.LEFT);

                if (!criteria.languages().isEmpty()) {
                    var languageEntityJoin = languageJoin.join("hdLanguages", jakarta.persistence.criteria.JoinType.LEFT);
                    predicates.add(cb.lower(languageEntityJoin.get("name")).in(criteria.languages()));
                }

                criteria.languageProficiencyOptional().ifPresent(proficiency -> {
                    var proficiencyJoin = languageJoin.join("hdLanguageProficiency",
                            jakarta.persistence.criteria.JoinType.LEFT);
                    predicates.add(cb.equal(cb.lower(proficiencyJoin.get("proficiencyLabel")), proficiency));
                });
            }

            if (!criteria.experienceRanges().isEmpty()) {
                addRangePredicates(predicates, criteria.experienceRanges(), root.get("numberOfExperience"), cb);
            }

            if (!criteria.projectCompletionRanges().isEmpty()) {
                addRangePredicates(predicates, criteria.projectCompletionRanges(), root.get("totalProjectCompletion"),
                        cb);
            }

            criteria.hourlyRateMinOptional()
                    .ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("hourlyRate"), min)));

            criteria.hourlyRateMaxOptional()
                    .ifPresent(max -> predicates.add(cb.lessThanOrEqualTo(root.get("hourlyRate"), max)));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void addRangePredicates(List<Predicate> predicates,
                                    List<RangeFilter> ranges,
                                    Path<Long> path,
                                    CriteriaBuilder cb) {
        List<Predicate> rangePredicates = ranges.stream()
                .map(range -> buildRangePredicate(range, path, cb))
                .filter(Objects::nonNull)
                .toList();
        if (!rangePredicates.isEmpty()) {
            predicates.add(cb.or(rangePredicates.toArray(Predicate[]::new)));
        }
    }

    private Predicate buildRangePredicate(RangeFilter range, Path<Long> path, CriteriaBuilder cb) {
        if (range == null) {
            return null;
        }
        if (range.hasMin() && range.hasMax()) {
            return cb.between(path, range.min(), range.max());
        }
        if (range.hasMin()) {
            return cb.greaterThanOrEqualTo(path, range.min());
        }
        if (range.hasMax()) {
            return cb.lessThanOrEqualTo(path, range.max());
        }
        return null;
    }

    private <T, R> R safeExtract(T source, Function<T, R> extractor, R fallback) {
        if (source == null) {
            return fallback;
        }
        R result = extractor.apply(source);
        return result != null ? result : fallback;
    }
}
