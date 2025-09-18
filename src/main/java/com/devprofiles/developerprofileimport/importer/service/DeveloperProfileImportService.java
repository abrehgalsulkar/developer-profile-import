package com.devprofiles.developerprofileimport.importer.service;

import com.devprofiles.developerprofileimport.domain.HdDeveloperCertification;
import com.devprofiles.developerprofileimport.domain.HdDeveloperEducation;
import com.devprofiles.developerprofileimport.domain.HdDeveloperExperience;
import com.devprofiles.developerprofileimport.domain.HdDeveloperFamilyMemberDetails;
import com.devprofiles.developerprofileimport.domain.HdDeveloperKnownLanguages;
import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.domain.HdDeveloperProjects;
import com.devprofiles.developerprofileimport.domain.HdLanguages;
import com.devprofiles.developerprofileimport.importer.DeveloperProfileRowMapper;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRowParseResult;
import com.devprofiles.developerprofileimport.importer.dto.RowIssueDetail;
import com.devprofiles.developerprofileimport.importer.parser.DeveloperProfileFileParserResolver;
import com.devprofiles.developerprofileimport.repository.HdDeveloperCertificationRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperEducationRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperExperienceRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperFamilyMemberDetailsRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperKnownLanguagesRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProfileRepository;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProjectsRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DeveloperProfileImportService {

    private static final Logger log = LoggerFactory.getLogger(DeveloperProfileImportService.class);

    private final DeveloperProfileFileParserResolver parserResolver;
    private final DeveloperProfileRowMapper rowMapper;
    private final ReferenceLookupService referenceLookupService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final HdDeveloperProfileRepository developerProfileRepository;
    private final HdDeveloperExperienceRepository experienceRepository;
    private final HdDeveloperProjectsRepository projectsRepository;
    private final HdDeveloperEducationRepository educationRepository;
    private final HdDeveloperCertificationRepository certificationRepository;
    private final HdDeveloperKnownLanguagesRepository knownLanguagesRepository;
    private final HdDeveloperFamilyMemberDetailsRepository familyMemberRepository;
    private final PlatformTransactionManager transactionManager;

    public DeveloperProfileImportReport importFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file must not be empty");
        }
        try (InputStream inputStream = file.getInputStream()) {
            return importStream(file.getOriginalFilename(), inputStream);
        }
    }

    public DeveloperProfileImportReport importStream(String filename, InputStream inputStream) throws IOException {
        List<DeveloperProfileRawRow> rawRows = parserResolver.parse(filename, inputStream);
        DeveloperProfileImportReport report = new DeveloperProfileImportReport();
        report.setTotalRows(rawRows.size());

        ReferenceLookupService.Session lookupSession = referenceLookupService.startSession();
        DuplicateDetectionService.Session duplicateSession = duplicateDetectionService.startSession();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        int completedCount = 0;
        for (DeveloperProfileRawRow rawRow : rawRows) {
            DeveloperProfileRowParseResult parseResult = rowMapper.map(rawRow);
            if (parseResult.hasErrors() || parseResult.getRow().isEmpty()) {
                RowIssueDetail detail = new RowIssueDetail(rawRow.getRowNumber());
                parseResult.getErrors().forEach(detail::addMessage);
                report.addIncompleteRowDetail(detail);
                continue;
            }

            DeveloperProfileImportRow row = parseResult.getRow().orElseThrow();
            Optional<String> duplicateMessage = duplicateSession.detectDuplicate(row);
            if (duplicateMessage.isPresent()) {
                RowIssueDetail detail = new RowIssueDetail(row.getRowNumber()).addMessage(duplicateMessage.get());
                report.addDuplicateRowDetail(detail);
                continue;
            }

            try {
                transactionTemplate.execute(status -> {
                    persistRow(row, lookupSession);
                    return null;
                });
                completedCount++;
            } catch (RuntimeException ex) {
                log.warn("Failed to persist row {}", row.getRowNumber(), ex);
                RowIssueDetail detail = new RowIssueDetail(row.getRowNumber())
                    .addMessage("Failed to persist developer profile: " + ex.getMessage());
                report.addIncompleteRowDetail(detail);
            }
        }

        report.setCompletedRows(completedCount);
        report.setDuplicateRows(report.getDuplicateRowDetails().size());
        report.setIncompleteRows(report.getIncompleteRowDetails().size());
        return report;
    }

    private void persistRow(DeveloperProfileImportRow row, ReferenceLookupService.Session lookupSession) {
        HdDeveloperProfile profile = buildProfile(row, lookupSession);
        HdDeveloperProfile savedProfile = developerProfileRepository.save(profile);
        saveLanguages(row, lookupSession, savedProfile);
        saveExperiences(row, lookupSession, savedProfile);
        saveProjects(row, lookupSession, savedProfile);
        saveEducations(row, savedProfile);
        saveCertifications(row, savedProfile);
        saveFamilyMembers(row, savedProfile);
    }

    private HdDeveloperProfile buildProfile(DeveloperProfileImportRow row, ReferenceLookupService.Session lookupSession) {
        HdDeveloperProfile profile = new HdDeveloperProfile();
        profile.setFirstName(row.getFirstName());
        profile.setLastName(row.getLastName());
        profile.setProfilePictureUrl(row.getProfilePictureUrl());
        profile.setIntroductionVideoUrl(row.getIntroductionVideoUrl());
        profile.setResumeUrl(row.getResumeUrl());
        profile.setIsVerified(row.getVerified());
        profile.setJobTitle(row.getJobTitle());
        profile.setHourlyRate(row.getHourlyRate());
        profile.setAbout(row.getAbout());
        profile.setNumberOfExperience(row.getExperienceYears());
        profile.setTotalWorkedHours(row.getTotalWorkedHours());
        profile.setTotalProjectCompletion(row.getTotalProjectCompletion());
        profile.setPermanentAddress(row.getPermanentAddress());
        profile.setTemporaryAddress(row.getTemporaryAddress());

        lookupSession.resolveDesignation(row.getDesignation()).ifPresent(profile::setDesignation);
        profile.setAvailabilities(resolveList(row.getAvailabilities(), lookupSession::resolveAvailability));
        profile.setWorkLocations(resolveList(row.getWorkLocations(), lookupSession::resolveWorkLocation));
        profile.setOverallExperienceSkills(resolveList(row.getSkills(), lookupSession::resolveTechnology));

        double averageRating = row.getReviews().stream()
            .map(DeveloperProfileImportRow.ReviewEntry::getRating)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(Double.NaN);
        long ratingCount = row.getReviews().stream()
            .map(DeveloperProfileImportRow.ReviewEntry::getRating)
            .filter(Objects::nonNull)
            .count();
        profile.setAverageRating(Double.isNaN(averageRating) ? null : averageRating);
        profile.setTotalReview(ratingCount == 0 ? null : ratingCount);
        return profile;
    }

    private void saveLanguages(DeveloperProfileImportRow row,
                               ReferenceLookupService.Session lookupSession,
                               HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.LanguageEntry entry : row.getLanguages()) {
            if (!StringUtils.hasText(entry.getLanguage())) {
                continue;
            }
            Optional<HdLanguages> language = lookupSession.resolveLanguage(entry.getLanguage());
            if (language.isEmpty()) {
                continue;
            }
            HdDeveloperKnownLanguages knownLanguage = new HdDeveloperKnownLanguages();
            knownLanguage.setHdDeveloperProfile(savedProfile);
            knownLanguage.setHdLanguages(language.get());
            lookupSession.resolveProficiency(entry.getProficiency()).ifPresent(knownLanguage::setHdLanguageProficiency);
            knownLanguagesRepository.save(knownLanguage);
        }
    }

    private void saveExperiences(DeveloperProfileImportRow row,
                                 ReferenceLookupService.Session lookupSession,
                                 HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.ExperienceEntry entry : row.getExperiences()) {
            if (isExperienceBlank(entry)) {
                continue;
            }
            HdDeveloperExperience experience = new HdDeveloperExperience();
            experience.setHdDeveloperProfile(savedProfile);
            experience.setCompanyName(trim(entry.getCompanyName()));
            experience.setJobTitle(trim(entry.getDesignation()));
            experience.setResponsibility(trim(entry.getResponsibilities()));
            experience.setStartDate(entry.getStartDate());
            experience.setEndDate(entry.getEndDate());
            experience.setCompanyLogo(trim(entry.getCompanyLogo()));
            experience.setTechnologies(resolveList(entry.getTechnologies(), lookupSession::resolveTechnology));
            experienceRepository.save(experience);
        }
    }

    private void saveProjects(DeveloperProfileImportRow row,
                               ReferenceLookupService.Session lookupSession,
                               HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.ProjectEntry entry : row.getProjects()) {
            if (isProjectBlank(entry)) {
                continue;
            }
            HdDeveloperProjects project = new HdDeveloperProjects();
            project.setHdDeveloperProfile(savedProfile);
            project.setProjectName(trim(entry.getProjectName()));
            project.setDeveloperRole(trim(entry.getDeveloperRole()));
            project.setResponsibility(trim(entry.getResponsibilities()));
            project.setDurationInMonths(entry.getDurationInMonths() == null ? null : entry.getDurationInMonths().longValue());
            project.setTechnologies(resolveList(entry.getTechnologies(), lookupSession::resolveTechnology));
            projectsRepository.save(project);
        }
    }

    private void saveEducations(DeveloperProfileImportRow row, HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.EducationEntry entry : row.getEducations()) {
            if (isEducationBlank(entry)) {
                continue;
            }
            HdDeveloperEducation education = new HdDeveloperEducation();
            education.setHdDeveloperProfile(savedProfile);
            education.setDegreeName(trim(entry.getDegreeName()));
            education.setInstitution(trim(entry.getInstitution()));
            education.setLocation(trim(entry.getLocation()));
            education.setCompletionYear(trim(entry.getCompletionYear()));
            educationRepository.save(education);
        }
    }

    private void saveCertifications(DeveloperProfileImportRow row, HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.CertificationEntry entry : row.getCertifications()) {
            if (isCertificationBlank(entry)) {
                continue;
            }
            HdDeveloperCertification certification = new HdDeveloperCertification();
            certification.setHdDeveloperProfile(savedProfile);
            certification.setCertificateName(trim(entry.getCertificationName()));
            certification.setInstitutionLogo(trim(entry.getInstitutionLogo()));
            certification.setCompletionYear(trim(entry.getCompletionYear()));
            certificationRepository.save(certification);
        }
    }

    private void saveFamilyMembers(DeveloperProfileImportRow row, HdDeveloperProfile savedProfile) {
        for (DeveloperProfileImportRow.FamilyMemberEntry entry : row.getFamilyMembers()) {
            if (isFamilyMemberBlank(entry)) {
                continue;
            }
            HdDeveloperFamilyMemberDetails member = new HdDeveloperFamilyMemberDetails();
            member.setHdDeveloperProfile(savedProfile);
            member.setName(trim(entry.getFullName()));
            member.setRelation(trim(entry.getRelation()));
            member.setContactNumber(trim(entry.getContactNumber()));
            familyMemberRepository.save(member);
        }
    }

    private <T> List<T> resolveList(List<String> values, Function<String, Optional<T>> resolver) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> resolved = new ArrayList<>();
        for (String value : values) {
            resolver.apply(value).ifPresent(resolved::add);
        }
        return resolved;
    }

    private boolean isExperienceBlank(DeveloperProfileImportRow.ExperienceEntry entry) {
        return !StringUtils.hasText(entry.getCompanyName())
            && !StringUtils.hasText(entry.getDesignation())
            && entry.getStartDate() == null
            && entry.getEndDate() == null
            && !StringUtils.hasText(entry.getResponsibilities())
            && entry.getTechnologies().isEmpty();
    }

    private boolean isProjectBlank(DeveloperProfileImportRow.ProjectEntry entry) {
        return !StringUtils.hasText(entry.getProjectName())
            && !StringUtils.hasText(entry.getDeveloperRole())
            && entry.getDurationInMonths() == null
            && entry.getTechnologies().isEmpty()
            && !StringUtils.hasText(entry.getResponsibilities());
    }

    private boolean isEducationBlank(DeveloperProfileImportRow.EducationEntry entry) {
        return !StringUtils.hasText(entry.getDegreeName())
            && !StringUtils.hasText(entry.getInstitution())
            && !StringUtils.hasText(entry.getLocation())
            && !StringUtils.hasText(entry.getCompletionYear());
    }

    private boolean isCertificationBlank(DeveloperProfileImportRow.CertificationEntry entry) {
        return !StringUtils.hasText(entry.getCertificationName())
            && !StringUtils.hasText(entry.getInstitutionLogo())
            && !StringUtils.hasText(entry.getCompletionYear());
    }

    private boolean isFamilyMemberBlank(DeveloperProfileImportRow.FamilyMemberEntry entry) {
        return !StringUtils.hasText(entry.getFullName())
            && !StringUtils.hasText(entry.getRelation())
            && !StringUtils.hasText(entry.getContactNumber());
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
