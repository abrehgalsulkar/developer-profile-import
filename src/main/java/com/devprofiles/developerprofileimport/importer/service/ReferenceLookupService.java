package com.devprofiles.developerprofileimport.importer.service;

import com.devprofiles.developerprofileimport.domain.HdAvailabilities;
import com.devprofiles.developerprofileimport.domain.HdDesignations;
import com.devprofiles.developerprofileimport.domain.HdExperienceRanges;
import com.devprofiles.developerprofileimport.domain.HdLanguageProficiency;
import com.devprofiles.developerprofileimport.domain.HdLanguages;
import com.devprofiles.developerprofileimport.domain.HdProjectCompletionRange;
import com.devprofiles.developerprofileimport.domain.HdRecentlyAddedLabels;
import com.devprofiles.developerprofileimport.domain.HdSkills;
import com.devprofiles.developerprofileimport.domain.HdTechnologies;
import com.devprofiles.developerprofileimport.domain.HdWorkLocations;
import com.devprofiles.developerprofileimport.repository.HdAvailabilitiesRepository;
import com.devprofiles.developerprofileimport.repository.HdDesignationsRepository;
import com.devprofiles.developerprofileimport.repository.HdExperienceRangesRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguageProficiencyRepository;
import com.devprofiles.developerprofileimport.repository.HdLanguagesRepository;
import com.devprofiles.developerprofileimport.repository.HdProjectCompletionRangeRepository;
import com.devprofiles.developerprofileimport.repository.HdRecentlyAddedLabelsRepository;
import com.devprofiles.developerprofileimport.repository.HdSkillsRepository;
import com.devprofiles.developerprofileimport.repository.HdTechnologiesRepository;
import com.devprofiles.developerprofileimport.repository.HdWorkLocationsRepository;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class ReferenceLookupService {

    private final HdTechnologiesRepository technologiesRepository;
    private final HdLanguagesRepository languagesRepository;
    private final HdLanguageProficiencyRepository proficiencyRepository;
    private final HdSkillsRepository skillsRepository;
    private final HdAvailabilitiesRepository availabilitiesRepository;
    private final HdWorkLocationsRepository workLocationsRepository;
    private final HdDesignationsRepository designationsRepository;
    private final HdExperienceRangesRepository experienceRangesRepository;
    private final HdProjectCompletionRangeRepository projectCompletionRangeRepository;
    private final HdRecentlyAddedLabelsRepository recentlyAddedLabelsRepository;

    public ReferenceLookupService(HdTechnologiesRepository technologiesRepository,
                                  HdLanguagesRepository languagesRepository,
                                  HdLanguageProficiencyRepository proficiencyRepository,
                                  HdSkillsRepository skillsRepository,
                                  HdAvailabilitiesRepository availabilitiesRepository,
                                  HdWorkLocationsRepository workLocationsRepository,
                                  HdDesignationsRepository designationsRepository,
                                  HdExperienceRangesRepository experienceRangesRepository,
                                  HdProjectCompletionRangeRepository projectCompletionRangeRepository,
                                  HdRecentlyAddedLabelsRepository recentlyAddedLabelsRepository) {
        this.technologiesRepository = technologiesRepository;
        this.languagesRepository = languagesRepository;
        this.proficiencyRepository = proficiencyRepository;
        this.skillsRepository = skillsRepository;
        this.availabilitiesRepository = availabilitiesRepository;
        this.workLocationsRepository = workLocationsRepository;
        this.designationsRepository = designationsRepository;
        this.experienceRangesRepository = experienceRangesRepository;
        this.projectCompletionRangeRepository = projectCompletionRangeRepository;
        this.recentlyAddedLabelsRepository = recentlyAddedLabelsRepository;
    }

    public Session startSession() {
        return new Session();
    }

    public class Session {
        private final Map<String, HdTechnologies> technologies = new HashMap<>();
        private final Map<String, HdLanguages> languages = new HashMap<>();
        private final Map<String, HdLanguageProficiency> proficiencies = new HashMap<>();
        private final Map<String, HdSkills> skills = new HashMap<>();
        private final Map<String, HdAvailabilities> availabilities = new HashMap<>();
        private final Map<String, HdWorkLocations> workLocations = new HashMap<>();
        private final Map<String, HdDesignations> designations = new HashMap<>();
        private final Map<String, HdExperienceRanges> experienceRanges = new HashMap<>();
        private final Map<String, HdProjectCompletionRange> projectCompletionRanges = new HashMap<>();
        private final Map<String, HdRecentlyAddedLabels> recentLabels = new HashMap<>();

        @Transactional
        public Optional<HdTechnologies> resolveTechnology(String value) {
            return resolve(value, technologies, technologiesRepository::findByTechnologyIgnoreCase, this::createTechnology);
        }

        @Transactional
        public Optional<HdLanguages> resolveLanguage(String value) {
            return resolve(value, languages, languagesRepository::findByNameIgnoreCase, this::createLanguage);
        }

        @Transactional
        public Optional<HdLanguageProficiency> resolveProficiency(String value) {
            return resolve(value, proficiencies, proficiencyRepository::findByProficiencyLabelIgnoreCase, this::createProficiency);
        }

        @Transactional
        public Optional<HdSkills> resolveSkill(String value) {
            return resolve(value, skills, skillsRepository::findByNameIgnoreCase, this::createSkill);
        }

        @Transactional
        public Optional<HdAvailabilities> resolveAvailability(String value) {
            return resolve(value, availabilities, availabilitiesRepository::findByAvailabilityLabelIgnoreCase, this::createAvailability);
        }

        @Transactional
        public Optional<HdWorkLocations> resolveWorkLocation(String value) {
            return resolve(value, workLocations, workLocationsRepository::findByWorkLocationLabelIgnoreCase, this::createWorkLocation);
        }

        @Transactional
        public Optional<HdDesignations> resolveDesignation(String value) {
            return resolve(value, designations, designationsRepository::findByDesignationIgnoreCase, this::createDesignation);
        }

        @Transactional
        public Optional<HdExperienceRanges> resolveExperienceRange(String value) {
            return resolve(value, experienceRanges, experienceRangesRepository::findByExperienceRangeLabelIgnoreCase, this::createExperienceRange);
        }

        @Transactional
        public Optional<HdProjectCompletionRange> resolveProjectCompletionRange(String value) {
            return resolve(value, projectCompletionRanges, projectCompletionRangeRepository::findByProjectCompletionRangeLabelIgnoreCase, this::createProjectCompletionRange);
        }

        @Transactional
        public Optional<HdRecentlyAddedLabels> resolveRecentlyAddedLabel(String value) {
            return resolve(value, recentLabels, recentlyAddedLabelsRepository::findByRecentlyAddedLabelsIgnoreCase, this::createRecentlyAddedLabel);
        }

        private Optional<HdTechnologies> createTechnology(String value) {
            HdTechnologies entity = new HdTechnologies();
            entity.setTechnology(value);
            return Optional.of(technologiesRepository.save(entity));
        }

        private Optional<HdLanguages> createLanguage(String value) {
            HdLanguages entity = new HdLanguages();
            entity.setName(value);
            return Optional.of(languagesRepository.save(entity));
        }

        private Optional<HdLanguageProficiency> createProficiency(String value) {
            HdLanguageProficiency entity = new HdLanguageProficiency();
            entity.setProficiencyLabel(value);
            return Optional.of(proficiencyRepository.save(entity));
        }

        private Optional<HdSkills> createSkill(String value) {
            HdSkills entity = new HdSkills();
            entity.setName(value);
            return Optional.of(skillsRepository.save(entity));
        }

        private Optional<HdAvailabilities> createAvailability(String value) {
            HdAvailabilities entity = new HdAvailabilities();
            entity.setAvailabilityLabel(value);
            return Optional.of(availabilitiesRepository.save(entity));
        }

        private Optional<HdWorkLocations> createWorkLocation(String value) {
            HdWorkLocations entity = new HdWorkLocations();
            entity.setWorkLocationLabel(value);
            return Optional.of(workLocationsRepository.save(entity));
        }

        private Optional<HdDesignations> createDesignation(String value) {
            HdDesignations entity = new HdDesignations();
            entity.setDesignation(value);
            return Optional.of(designationsRepository.save(entity));
        }

        private Optional<HdExperienceRanges> createExperienceRange(String value) {
            HdExperienceRanges entity = new HdExperienceRanges();
            entity.setExperienceRangeLabel(value);
            return Optional.of(experienceRangesRepository.save(entity));
        }

        private Optional<HdProjectCompletionRange> createProjectCompletionRange(String value) {
            HdProjectCompletionRange entity = new HdProjectCompletionRange();
            entity.setProjectCompletionRangeLabel(value);
            return Optional.of(projectCompletionRangeRepository.save(entity));
        }

        private Optional<HdRecentlyAddedLabels> createRecentlyAddedLabel(String value) {
            HdRecentlyAddedLabels entity = new HdRecentlyAddedLabels();
            entity.setRecentlyAddedLabels(value);
            return Optional.of(recentlyAddedLabelsRepository.save(entity));
        }

        private <T> Optional<T> resolve(String value,
                                        Map<String, T> cache,
                                        java.util.function.Function<String, Optional<T>> finder,
                                        java.util.function.Function<String, Optional<T>> creator) {
            if (!StringUtils.hasText(value)) {
                return Optional.empty();
            }
            String normalized = normalize(value);
            if (cache.containsKey(normalized)) {
                return Optional.ofNullable(cache.get(normalized));
            }
            Optional<T> existing = finder.apply(value.trim());
            if (existing.isPresent()) {
                cache.put(normalized, existing.get());
                return existing;
            }
            Optional<T> created = creator.apply(value.trim());
            created.ifPresent(entity -> cache.put(normalized, entity));
            return created;
        }

        private String normalize(String value) {
            return value.trim().toLowerCase(Locale.ENGLISH);
        }
    }
}
