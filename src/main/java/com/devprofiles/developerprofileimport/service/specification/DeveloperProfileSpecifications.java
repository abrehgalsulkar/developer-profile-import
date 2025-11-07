package com.devprofiles.developerprofileimport.service.specification;

import com.devprofiles.developerprofileimport.domain.HdDeveloperKnownLanguages;
import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.domain.HdLanguageProficiency;
import com.devprofiles.developerprofileimport.domain.HdLanguages;
import com.devprofiles.developerprofileimport.domain.HdTechnologies;
import com.devprofiles.developerprofileimport.service.dto.DeveloperFilterCriteria;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class DeveloperProfileSpecifications {

  private DeveloperProfileSpecifications() {
  }

  private static final Map<String, NumericRange> EXPERIENCE_RANGE_BY_LABEL = Map.of(
      "0-5", new NumericRange(0L, 5L),
      "6-10", new NumericRange(6L, 10L),
      "11-15", new NumericRange(11L, 15L),
      "16-20", new NumericRange(16L, 20L),
      "21+", new NumericRange(21L, null)
  );

  private static final Map<String, NumericRange> PROJECT_RANGE_BY_LABEL = Map.of(
      "0-5", new NumericRange(0L, 5L),
      "6-10", new NumericRange(6L, 10L),
      "11-15", new NumericRange(11L, 15L),
      "16-20", new NumericRange(16L, 20L),
      "21+", new NumericRange(21L, null)
  );

  public static Specification<HdDeveloperProfile> withFilter(DeveloperFilterCriteria criteria) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> predicates = new ArrayList<>();

      Optional.ofNullable(normalize(criteria.getSearchTerm()))
          .filter(StringUtils::hasText)
          .ifPresent(search -> {
            String like = "%" + search + "%";
            Predicate firstName = cb.like(cb.lower(root.get("firstName")), like);
            Predicate lastName = cb.like(cb.lower(root.get("lastName")), like);
            predicates.add(cb.or(firstName, lastName));
          });

      if (!criteria.getTechnologies().isEmpty()) {
        Join<HdDeveloperProfile, HdTechnologies> technologiesJoin =
            root.join("overallExperienceSkills", JoinType.LEFT);
    Expression<String> techName = cb.lower(technologiesJoin.get("technology"));
    predicates.add(techName.in(lowerCase(criteria.getTechnologies())));
      }

      if (!criteria.getAvailabilities().isEmpty()) {
        Join<HdDeveloperProfile, ?> availJoin = root.join("availabilities", JoinType.LEFT);
        Expression<String> availabilityLabel = cb.lower(availJoin.get("availabilityLabel"));
        predicates.add(availabilityLabel.in(lowerCase(criteria.getAvailabilities())));
      }

      if (!criteria.getWorkLocations().isEmpty()) {
        Join<HdDeveloperProfile, ?> workLocationsJoin = root.join("workLocations", JoinType.LEFT);
        Expression<String> workLocationLabel = cb.lower(workLocationsJoin.get("workLocationLabel"));
        predicates.add(workLocationLabel.in(lowerCase(criteria.getWorkLocations())));
      }

      appendRangePredicate(predicates, criteria.getExperienceRanges(), EXPERIENCE_RANGE_BY_LABEL,
          root.get("numberOfExperience").as(Long.class), cb);

      appendRangePredicate(predicates, criteria.getProjectCompletions(), PROJECT_RANGE_BY_LABEL,
          root.get("totalProjectCompletion").as(Long.class), cb);

      if (criteria.getMinHourlyRate() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("hourlyRate"), criteria.getMinHourlyRate()));
      }

      if (criteria.getMaxHourlyRate() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("hourlyRate"), criteria.getMaxHourlyRate()));
      }

      if (criteria.getVerified() != null) {
        predicates.add(cb.equal(root.get("isVerified"), criteria.getVerified()));
      }

      if (!criteria.getLanguages().isEmpty() || StringUtils.hasText(criteria.getLanguageProficiency())) {
        Join<HdDeveloperProfile, HdDeveloperKnownLanguages> knownLanguagesJoin =
            root.join("knownLanguages", JoinType.LEFT);

        if (!criteria.getLanguages().isEmpty()) {
          Join<HdDeveloperKnownLanguages, HdLanguages> languagesJoin =
              knownLanguagesJoin.join("hdLanguages", JoinType.LEFT);
          Expression<String> languageName = cb.lower(languagesJoin.get("name"));
          predicates.add(languageName.in(lowerCase(criteria.getLanguages())));
        }

        if (StringUtils.hasText(criteria.getLanguageProficiency())) {
          Join<HdDeveloperKnownLanguages, HdLanguageProficiency> proficiencyJoin =
              knownLanguagesJoin.join("hdLanguageProficiency", JoinType.LEFT);
          String proficiency = criteria.getLanguageProficiency().trim().toLowerCase(Locale.ENGLISH);
          predicates.add(cb.equal(cb.lower(proficiencyJoin.get("proficiencyLabel")), proficiency));
        }
      }

      return cb.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static void appendRangePredicate(List<Predicate> predicates,
      List<String> rangeLabels,
      Map<String, NumericRange> lookup,
      Expression<Long> field,
      jakarta.persistence.criteria.CriteriaBuilder cb) {

    if (rangeLabels == null || rangeLabels.isEmpty()) {
      return;
    }

    List<Predicate> rangePredicates = new ArrayList<>();
    for (String label : rangeLabels) {
      NumericRange range = lookup.get(normalize(label));
      if (range == null) {
        continue;
      }

      Predicate predicate;
      if (range.max() == null) {
        predicate = cb.greaterThanOrEqualTo(field, range.min());
      } else {
        predicate = cb.between(field, range.min(), range.max());
      }
      rangePredicates.add(predicate);
    }

    if (!rangePredicates.isEmpty()) {
      predicates.add(cb.or(rangePredicates.toArray(Predicate[]::new)));
    }
  }

  private static List<String> lowerCase(List<String> values) {
    return values.stream()
        .map(DeveloperProfileSpecifications::normalize)
        .filter(StringUtils::hasText)
        .map(value -> value.toLowerCase(Locale.ENGLISH))
        .toList();
  }

  private static String normalize(String value) {
    return value == null ? "" : value.trim();
  }

  private record NumericRange(Long min, Long max) {
  }
}

