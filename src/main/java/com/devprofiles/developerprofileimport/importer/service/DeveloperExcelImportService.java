package com.devprofiles.developerprofileimport.importer.service;

import com.devprofiles.developerprofileimport.domain.*;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportReport;
import com.devprofiles.developerprofileimport.importer.dto.RowIssueDetail;
import com.devprofiles.developerprofileimport.importer.excel.DeveloperAggregate;
import com.devprofiles.developerprofileimport.importer.excel.ExcelImportPreview;
import com.devprofiles.developerprofileimport.importer.excel.ExcelTemplateReader;
import com.devprofiles.developerprofileimport.repository.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DeveloperExcelImportService {

    private final ExcelTemplateReader excelTemplateReader;
    private final ReferenceLookupService referenceLookupService;
    private final DuplicateDetectionService duplicateDetectionService;

    private final HdDeveloperProfileRepository profileRepository;
    private final HdDeveloperExperienceRepository experienceRepository;
    private final HdDeveloperProjectsRepository projectsRepository;
    private final HdDeveloperEducationRepository educationRepository;
    private final HdDeveloperCertificationRepository certificationRepository;
    private final HdDeveloperKnownLanguagesRepository knownLanguagesRepository;
    private final HdDeveloperFamilyMemberDetailsRepository familyRepository;

    private final PlatformTransactionManager txManager;

    public DeveloperProfileImportReport importExcel(MultipartFile workbook, MultipartFile assetsZip, boolean dryRun)
            throws IOException {
        DeveloperProfileImportReport report = new DeveloperProfileImportReport();
        ExcelImportPreview preview;
        try (InputStream is = workbook.getInputStream()) {
            preview = excelTemplateReader.parse(is);
        }
        Map<String, byte[]> assets = readAssetsZip(assetsZip);

        ReferenceLookupService.Session lookups = referenceLookupService.startSession();
        DuplicateDetectionService.Session dups = duplicateDetectionService.startSession();
        TransactionTemplate tx = new TransactionTemplate(txManager);

        report.setTotalRows(preview.developers.size());
        int completed = 0;

        for (DeveloperAggregate agg : preview.developers) {
            List<String> errors = validate(agg);
            if (!errors.isEmpty()) {
                RowIssueDetail detail = new RowIssueDetail(0);
                errors.forEach(detail::addMessage);
                report.addIncompleteRowDetail(detail);
                continue;
            }
            
            var dupMsg = dups.detectDuplicate(toRowShim(agg));
            if (dupMsg.isPresent()) {
                RowIssueDetail detail = new RowIssueDetail(0).addMessage(dupMsg.get());
                report.addDuplicateRowDetail(detail);
                continue;
            }

            try {
                tx.execute(status -> {
                    HdDeveloperProfile profile = mapProfile(agg, lookups, assets, report);
                    HdDeveloperProfile saved = profileRepository.save(profile);
                    saveLanguages(agg, lookups, saved);
                    saveExperiences(agg, lookups, assets, saved, report);
                    saveProjects(agg, lookups, saved);
                    saveEducations(agg, saved);
                    saveCertifications(agg, saved);
                    saveFamily(agg, saved);
                    return null;
                });
                completed++;
            } catch (RuntimeException ex) {
                RowIssueDetail detail = new RowIssueDetail(0)
                        .addMessage("Failed to persist developer: " + ex.getMessage());
                report.addIncompleteRowDetail(detail);
            }
        }
        report.setCompletedRows(completed);
        report.setDuplicateRows(report.getDuplicateRowDetails().size());
        report.setIncompleteRows(report.getIncompleteRowDetails().size());
        return report;
    }

    private Map<String, byte[]> readAssetsZip(MultipartFile assetsZip) throws IOException {
        Map<String, byte[]> map = new HashMap<>();
        if (assetsZip == null || assetsZip.isEmpty())
            return map;
        try (ZipInputStream zis = new ZipInputStream(assetsZip.getInputStream())) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if (e.isDirectory())
                    continue;
                String name = Paths.get(e.getName()).getFileName().toString();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                map.put(name, baos.toByteArray());
            }
        }
        return map;
    }

    private HdDeveloperProfile mapProfile(DeveloperAggregate agg, ReferenceLookupService.Session lookups,
            Map<String, byte[]> assets, DeveloperProfileImportReport report) {
        HdDeveloperProfile p = new HdDeveloperProfile();
        p.setFirstName(agg.firstName);
        p.setLastName(agg.lastName);
        p.setJobTitle(agg.jobTitle);
        p.setAbout(agg.about);
        p.setHourlyRate(agg.hourlyRate);
        p.setNumberOfExperience(agg.experienceYears);
        p.setTotalWorkedHours(agg.totalWorkedHours);
        p.setTotalProjectCompletion(agg.totalProjectCompletion);
        p.setIsVerified(agg.verifiedDeveloper);
        p.setPermanentAddress(agg.permanentAddress);
        p.setTemporaryAddress(agg.temporaryAddress);
        if (StringUtils.hasText(agg.designation)) {
            lookups.resolveDesignation(agg.designation).ifPresent(p::setDesignation);
        }
        p.setAvailabilities(resolveList(agg.availabilities, lookups::resolveAvailability));
        p.setWorkLocations(resolveList(agg.workLocations, lookups::resolveWorkLocation));
        p.setOverallExperienceSkills(resolveList(agg.skills, lookups::resolveTechnology));

        double avg = agg.reviews.stream().map(r -> r.rating).filter(Objects::nonNull).mapToDouble(Double::doubleValue)
                .average().orElse(Double.NaN);
        long cnt = agg.reviews.stream().map(r -> r.rating).filter(Objects::nonNull).count();
        p.setAverageRating(Double.isNaN(avg) ? null : avg);
        p.setTotalReview(cnt == 0 ? null : cnt);

        String base = "storage/developers/" + agg.developerKey + "/";
        if (StringUtils.hasText(agg.profilePictureFile)) {
            String rel = base + agg.profilePictureFile;
            if (writeAsset(agg.profilePictureFile, assets, rel, 2 * 1024 * 1024, report)) {
                p.setProfilePictureUrl(rel);
            }
        }
        if (StringUtils.hasText(agg.introVideoFile)) {
            String rel = base + agg.introVideoFile;
            if (writeAsset(agg.introVideoFile, assets, rel, 20 * 1024 * 1024, report)) {
                p.setIntroductionVideoUrl(rel);
            }
        }
        if (StringUtils.hasText(agg.cvFile)) {
            String rel = base + agg.cvFile;
            if (writeAsset(agg.cvFile, assets, rel, 2 * 1024 * 1024, report)) {
                p.setResumeUrl(rel);
            }
        }
        return p;
    }

    private boolean writeAsset(String fileName, Map<String, byte[]> assets, String relativeTarget, long maxBytes,
            DeveloperProfileImportReport report) {
        byte[] content = assets.get(fileName);
        if (content == null) {
            report.addWarning(new RowIssueDetail(0).addMessage("Missing asset in ZIP: " + fileName));
            return false;
        }
        if (content.length > maxBytes) {
            report.addWarning(new RowIssueDetail(0).addMessage("Asset exceeds size limit: " + fileName));
            return false;
        }
        try {
            Path target = Paths.get(relativeTarget);
            Files.createDirectories(target.getParent());
            Files.write(target, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException ex) {
            report.addWarning(
                    new RowIssueDetail(0).addMessage("Failed to write asset: " + fileName + ": " + ex.getMessage()));
            return false;
        }
    }

    private void saveLanguages(DeveloperAggregate agg, ReferenceLookupService.Session lookups,
            HdDeveloperProfile saved) {
        for (int i = 0; i < agg.languages.size(); i++) {
            String lang = agg.languages.get(i);
            if (!StringUtils.hasText(lang))
                continue;
            Optional<HdLanguages> l = lookups.resolveLanguage(lang);
            if (l.isEmpty())
                continue;
            HdDeveloperKnownLanguages kl = new HdDeveloperKnownLanguages();
            kl.setHdDeveloperProfile(saved);
            kl.setHdLanguages(l.get());
            String prof = i < agg.languageProficiencies.size() ? agg.languageProficiencies.get(i) : null;
            lookups.resolveProficiency(prof).ifPresent(kl::setHdLanguageProficiency);
            knownLanguagesRepository.save(kl);
        }
    }

    private void saveExperiences(DeveloperAggregate agg, ReferenceLookupService.Session lookups,
            Map<String, byte[]> assets, HdDeveloperProfile saved, DeveloperProfileImportReport report) {
        String base = "storage/developers/" + agg.developerKey + "/";
        for (DeveloperAggregate.Experience eagg : agg.experiences) {
            HdDeveloperExperience e = new HdDeveloperExperience();
            e.setHdDeveloperProfile(saved);
            e.setCompanyName(eagg.companyName);
            e.setJobTitle(eagg.designation);
            e.setResponsibility(eagg.responsibilities);
            e.setStartDate(eagg.startDate);
            e.setEndDate(eagg.endDate);
            if (StringUtils.hasText(eagg.companyLogoFile)) {
                String rel = base + eagg.companyLogoFile;
                if (writeAsset(eagg.companyLogoFile, assets, rel, 2 * 1024 * 1024, report)) {
                    e.setCompanyLogo(rel);
                }
            }
            e.setTechnologies(resolveList(eagg.technologies, lookups::resolveTechnology));
            experienceRepository.save(e);
        }
    }

    private void saveProjects(DeveloperAggregate agg, ReferenceLookupService.Session lookups,
            HdDeveloperProfile saved) {
        for (DeveloperAggregate.Project pAgg : agg.projects) {
            HdDeveloperProjects p = new HdDeveloperProjects();
            p.setHdDeveloperProfile(saved);
            p.setProjectName(pAgg.projectName);
            p.setDeveloperRole(pAgg.developerRole);
            p.setDurationInMonths(pAgg.durationMonths == null ? null : pAgg.durationMonths.longValue());
            p.setResponsibility(pAgg.responsibilities);
            p.setTechnologies(resolveList(pAgg.technologies, lookups::resolveTechnology));
            projectsRepository.save(p);
        }
    }

    private void saveEducations(DeveloperAggregate agg, HdDeveloperProfile saved) {
        for (DeveloperAggregate.Education eAgg : agg.educations) {
            HdDeveloperEducation e = new HdDeveloperEducation();
            e.setHdDeveloperProfile(saved);
            e.setDegreeName(eAgg.degreeName);
            e.setInstitution(eAgg.institution);
            e.setLocation(eAgg.location);
            e.setCompletionYear(eAgg.completionYear);
            educationRepository.save(e);
        }
    }

    private void saveCertifications(DeveloperAggregate agg, HdDeveloperProfile saved) {
        for (DeveloperAggregate.Certification cAgg : agg.certifications) {
            HdDeveloperCertification c = new HdDeveloperCertification();
            c.setHdDeveloperProfile(saved);
            c.setCertificateName(cAgg.certificationName);
            c.setInstitutionLogo(cAgg.institutionLogoFile);
            c.setCompletionYear(cAgg.completionYear);
            certificationRepository.save(c);
        }
    }

    private void saveFamily(DeveloperAggregate agg, HdDeveloperProfile saved) {
        for (DeveloperAggregate.FamilyMember fAgg : agg.family) {
            HdDeveloperFamilyMemberDetails f = new HdDeveloperFamilyMemberDetails();
            f.setHdDeveloperProfile(saved);
            f.setName(fAgg.fullName);
            f.setRelation(fAgg.relation);
            f.setContactNumber(fAgg.contactNumber);
            familyRepository.save(f);
        }
    }

    private <T> List<T> resolveList(List<String> values, java.util.function.Function<String, Optional<T>> resolver) {
        if (values == null || values.isEmpty())
            return Collections.emptyList();
        List<T> list = new ArrayList<>();
        for (String v : values)
            resolver.apply(v).ifPresent(list::add);
        return list;
    }

    private List<String> validate(DeveloperAggregate agg) {
        List<String> errs = new ArrayList<>();
        if (!StringUtils.hasText(agg.firstName))
            errs.add("FirstName is required");
        if (!StringUtils.hasText(agg.lastName))
            errs.add("LastName is required");
        if (!StringUtils.hasText(agg.designation))
            errs.add("Designation is required");
        if (agg.hourlyRate != null && agg.hourlyRate < 0)
            errs.add("HourlyRate must be >= 0");
        if (agg.experienceYears != null && agg.experienceYears < 0)
            errs.add("ExperienceYears must be >= 0");
        for (DeveloperAggregate.Project p : agg.projects) {
            if (p.durationMonths != null && p.durationMonths < 0)
                errs.add("Project duration must be >= 0");
        }
        for (DeveloperAggregate.Review r : agg.reviews) {
            if (r.rating != null && (r.rating < 1 || r.rating > 5))
                errs.add("Review rating must be 1..5");
        }
        return errs;
    }

    private com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow toRowShim(
            DeveloperAggregate agg) {
        var row = new com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow();
        row.setFirstName(agg.firstName);
        row.setLastName(agg.lastName);
        row.setDesignation(agg.designation);
        return row;
    }


    public DeveloperProfileImportReport importExcel(MultipartFile workbook, MultipartFile assetsZip) throws IOException {
        return importExcel(workbook, assetsZip, false);
    }

}

