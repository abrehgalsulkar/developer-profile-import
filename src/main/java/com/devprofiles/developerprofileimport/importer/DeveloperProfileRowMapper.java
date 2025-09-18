package com.devprofiles.developerprofileimport.importer;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRowParseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DeveloperProfileRowMapper {

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    private final ObjectMapper objectMapper;

    public DeveloperProfileRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DeveloperProfileRowParseResult map(DeveloperProfileRawRow rawRow) {
        DeveloperProfileRowParseResult result = new DeveloperProfileRowParseResult();
        DeveloperProfileImportRow row = new DeveloperProfileImportRow();
        row.setRowNumber(rawRow.getRowNumber());

        row.setFirstName(requireText(rawRow, DeveloperProfileImportHeaders.FIRST_NAME, result));
        row.setLastName(requireText(rawRow, DeveloperProfileImportHeaders.LAST_NAME, result));
        row.setProfilePictureUrl(readValue(rawRow, DeveloperProfileImportHeaders.PROFILE_PICTURE));
        row.setIntroductionVideoUrl(readValue(rawRow, DeveloperProfileImportHeaders.INTRO_VIDEO));
        row.setResumeUrl(readValue(rawRow, DeveloperProfileImportHeaders.DEVELOPER_CV));
        row.setVerified(parseBoolean(readValue(rawRow, DeveloperProfileImportHeaders.VERIFIED)));
        row.setDesignation(readValue(rawRow, DeveloperProfileImportHeaders.DESIGNATION));
        row.setJobTitle(readValue(rawRow, DeveloperProfileImportHeaders.JOB_TITLE));
        row.setHourlyRate(parseDouble(readValue(rawRow, DeveloperProfileImportHeaders.HOURLY_RATE), DeveloperProfileImportHeaders.HOURLY_RATE, result));
        row.setAbout(readValue(rawRow, DeveloperProfileImportHeaders.ABOUT));
        row.setExperienceYears(parseLong(readValue(rawRow, DeveloperProfileImportHeaders.EXPERIENCE_YEARS), DeveloperProfileImportHeaders.EXPERIENCE_YEARS, result));
        row.setTotalWorkedHours(parseLong(readValue(rawRow, DeveloperProfileImportHeaders.TOTAL_WORKED_HOURS), DeveloperProfileImportHeaders.TOTAL_WORKED_HOURS, result));
        row.setTotalProjectCompletion(parseLong(readValue(rawRow, DeveloperProfileImportHeaders.TOTAL_PROJECT_COMPLETION), DeveloperProfileImportHeaders.TOTAL_PROJECT_COMPLETION, result));
        row.setAvailabilities(splitSimpleList(readValue(rawRow, DeveloperProfileImportHeaders.AVAILABILITIES)));
        row.setWorkLocations(splitSimpleList(readValue(rawRow, DeveloperProfileImportHeaders.WORK_LOCATIONS)));
        row.setSkills(splitSimpleList(readValue(rawRow, DeveloperProfileImportHeaders.SKILLS)));
        row.setLanguages(parseLanguages(readValue(rawRow, DeveloperProfileImportHeaders.LANGUAGES), result));
        row.setPermanentAddress(readValue(rawRow, DeveloperProfileImportHeaders.PERMANENT_ADDRESS));
        row.setTemporaryAddress(readValue(rawRow, DeveloperProfileImportHeaders.TEMPORARY_ADDRESS));
        row.setExperiences(parseExperiences(readValue(rawRow, DeveloperProfileImportHeaders.EXPERIENCES), result));
        row.setProjects(parseProjects(readValue(rawRow, DeveloperProfileImportHeaders.PROJECTS), result));
        row.setEducations(parseEducations(readValue(rawRow, DeveloperProfileImportHeaders.EDUCATIONS), result));
        row.setCertifications(parseCertifications(readValue(rawRow, DeveloperProfileImportHeaders.CERTIFICATIONS), result));
        row.setReviews(parseReviews(readValue(rawRow, DeveloperProfileImportHeaders.REVIEWS), result));
        row.setFamilyMembers(parseFamilyMembers(readValue(rawRow, DeveloperProfileImportHeaders.FAMILY_MEMBERS), result));

        if (!result.hasErrors()) {
            result.row(row);
        }
        return result;
    }

    private String readValue(DeveloperProfileRawRow rawRow, String key) {
        String value = rawRow.getValue(key);
        return value != null ? value.trim() : null;
    }

    private String requireText(DeveloperProfileRawRow rawRow, String key, DeveloperProfileRowParseResult result) {
        String value = readValue(rawRow, key);
        if (isBlankish(value)) {
            result.addError(key + " is required");
        }
        return value;
    }
    private Boolean parseBoolean(String value) {
        if (isBlankish(value)) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.equals("true") || normalized.equals("yes") || normalized.equals("1")) {
            return Boolean.TRUE;
        }
        if (normalized.equals("false") || normalized.equals("no") || normalized.equals("0")) {
            return Boolean.FALSE;
        }
        return null;
    }

    private Long parseLong(String value, String field, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            result.addError(field + " must be a whole number");
            return null;
        }
    }

    private Double parseDouble(String value, String field, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            result.addError(field + " must be numeric");
            return null;
        }
    }

    private Integer parseInteger(String value, String field, DeveloperProfileRowParseResult result) {
        Long parsed = parseLong(value, field, result);
        if (parsed == null) {
            return null;
        }
        if (parsed > Integer.MAX_VALUE) {
            result.addError(field + " exceeds allowed range");
            return null;
        }
        return parsed.intValue();
    }

    private List<String> splitSimpleList(String value) {
        if (isBlankish(value)) {
            return List.of();
        }
        String[] tokens = value.split("[;,]");
        Set<String> ordered = new LinkedHashSet<>();
        for (String token : tokens) {
            if (!isBlankish(token)) {
                ordered.add(token.trim());
            }
        }
        return List.copyOf(ordered);
    }

    private List<DeveloperProfileImportRow.LanguageEntry> parseLanguages(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.LanguageEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.LanguageEntry entry = new DeveloperProfileImportRow.LanguageEntry();
                        entry.setLanguage(textOrNull(node, "language", "name"));
                        entry.setProficiency(textOrNull(node, "proficiency", "level"));
                        if (StringUtils.hasText(entry.getLanguage())) {
                            entries.add(entry);
                        }
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Languages column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.LanguageEntry entry = new DeveloperProfileImportRow.LanguageEntry();
                entry.setLanguage(parts[0].trim());
                if (parts.length > 1) {
                    entry.setProficiency(parts[1].trim());
                }
                if (StringUtils.hasText(entry.getLanguage())) {
                    entries.add(entry);
                }
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.ExperienceEntry> parseExperiences(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.ExperienceEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.ExperienceEntry entry = new DeveloperProfileImportRow.ExperienceEntry();
                        entry.setCompanyName(textOrNull(node, "companyName", "company"));
                        entry.setDesignation(textOrNull(node, "designation", "title"));
                        entry.setStartDate(parseDate(textOrNull(node, "startDate", "from"), "Experience start date", result));
                        entry.setEndDate(parseDate(textOrNull(node, "endDate", "to"), "Experience end date", result));
                        entry.setTechnologies(readStringList(node.get("technologies")));
                        entry.setResponsibilities(textOrNull(node, "responsibilities", "responsibility"));
                        entry.setCompanyLogo(textOrNull(node, "companyLogo", "logo"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Experiences column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.ExperienceEntry entry = new DeveloperProfileImportRow.ExperienceEntry();
                if (parts.length > 0) {
                    entry.setCompanyName(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setDesignation(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setStartDate(parseDate(parts[2].trim(), "Experience start date", result));
                }
                if (parts.length > 3) {
                    entry.setEndDate(parseDate(parts[3].trim(), "Experience end date", result));
                }
                if (parts.length > 4) {
                    entry.setTechnologies(splitSimpleList(parts[4]));
                }
                if (parts.length > 5) {
                    entry.setResponsibilities(parts[5].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.ProjectEntry> parseProjects(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.ProjectEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.ProjectEntry entry = new DeveloperProfileImportRow.ProjectEntry();
                        entry.setProjectName(textOrNull(node, "projectName", "name"));
                        entry.setDeveloperRole(textOrNull(node, "developerRole", "role"));
                        entry.setDurationInMonths(parseInteger(textOrNull(node, "durationInMonths", "duration"), "Project duration", result));
                        entry.setTechnologies(readStringList(node.get("technologies")));
                        entry.setResponsibilities(textOrNull(node, "responsibilities", "responsibility"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Projects column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.ProjectEntry entry = new DeveloperProfileImportRow.ProjectEntry();
                if (parts.length > 0) {
                    entry.setProjectName(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setDeveloperRole(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setDurationInMonths(parseInteger(parts[2].trim(), "Project duration", result));
                }
                if (parts.length > 3) {
                    entry.setTechnologies(splitSimpleList(parts[3]));
                }
                if (parts.length > 4) {
                    entry.setResponsibilities(parts[4].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.EducationEntry> parseEducations(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.EducationEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.EducationEntry entry = new DeveloperProfileImportRow.EducationEntry();
                        entry.setDegreeName(textOrNull(node, "degreeName", "degree"));
                        entry.setCompletionYear(textOrNull(node, "completionYear", "year"));
                        entry.setInstitution(textOrNull(node, "institution", "school"));
                        entry.setLocation(textOrNull(node, "location", "city"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Educations column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.EducationEntry entry = new DeveloperProfileImportRow.EducationEntry();
                if (parts.length > 0) {
                    entry.setDegreeName(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setCompletionYear(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setInstitution(parts[2].trim());
                }
                if (parts.length > 3) {
                    entry.setLocation(parts[3].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.CertificationEntry> parseCertifications(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.CertificationEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.CertificationEntry entry = new DeveloperProfileImportRow.CertificationEntry();
                        entry.setInstitutionLogo(textOrNull(node, "institutionLogo", "logo"));
                        entry.setCertificationName(textOrNull(node, "certificationName", "name"));
                        entry.setCompletionYear(textOrNull(node, "completionYear", "year"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Certifications column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.CertificationEntry entry = new DeveloperProfileImportRow.CertificationEntry();
                if (parts.length > 0) {
                    entry.setInstitutionLogo(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setCertificationName(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setCompletionYear(parts[2].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.ReviewEntry> parseReviews(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.ReviewEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.ReviewEntry entry = new DeveloperProfileImportRow.ReviewEntry();
                        entry.setReviewerProfilePicture(textOrNull(node, "reviewerProfilePicture", "profilePicture"));
                        entry.setReviewerFullName(textOrNull(node, "reviewerFullName", "name"));
                        entry.setDesignation(textOrNull(node, "designation", "title"));
                        entry.setRating(parseDouble(textOrNull(node, "rating", "score"), "Review rating", result));
                        entry.setReview(textOrNull(node, "review", "comment"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Reviews column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.ReviewEntry entry = new DeveloperProfileImportRow.ReviewEntry();
                if (parts.length > 0) {
                    entry.setReviewerFullName(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setDesignation(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setRating(parseDouble(parts[2].trim(), "Review rating", result));
                }
                if (parts.length > 3) {
                    entry.setReview(parts[3].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private List<DeveloperProfileImportRow.FamilyMemberEntry> parseFamilyMembers(String value, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return List.of();
        }
        List<DeveloperProfileImportRow.FamilyMemberEntry> entries = new ArrayList<>();
        if (looksLikeJson(value)) {
            try {
                JsonNode root = objectMapper.readTree(value);
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        DeveloperProfileImportRow.FamilyMemberEntry entry = new DeveloperProfileImportRow.FamilyMemberEntry();
                        entry.setFullName(textOrNull(node, "fullName", "name"));
                        entry.setRelation(textOrNull(node, "relation", "relationship"));
                        entry.setContactNumber(textOrNull(node, "contactNumber", "contact"));
                        entries.add(entry);
                    }
                }
            } catch (JsonProcessingException ex) {
                result.addError("Family Members column contains invalid JSON");
            }
        }
        if (entries.isEmpty()) {
            for (String token : value.split(";")) {
                if (isBlankish(token)) {
                    continue;
                }
                String[] parts = token.split("\\|");
                DeveloperProfileImportRow.FamilyMemberEntry entry = new DeveloperProfileImportRow.FamilyMemberEntry();
                if (parts.length > 0) {
                    entry.setFullName(parts[0].trim());
                }
                if (parts.length > 1) {
                    entry.setRelation(parts[1].trim());
                }
                if (parts.length > 2) {
                    entry.setContactNumber(parts[2].trim());
                }
                entries.add(entry);
            }
        }
        return entries;
    }
    private boolean looksLikeJson(String value) {
        String trimmed = value.trim();
        return trimmed.startsWith("[") && trimmed.endsWith("]");
    }

    private String textOrNull(JsonNode node, String primaryKey, String fallbackKey) {
        if (node == null) {
            return null;
        }
        JsonNode target = node.get(primaryKey);
        if (target == null && fallbackKey != null) {
            target = node.get(fallbackKey);
        }
        return target != null && !target.isNull() ? target.asText() : null;
    }
    private boolean isBlankish(String value) {
        if (value == null) {
            return true;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return true;
        }
        if ("[]".equals(trimmed) || "{}".equals(trimmed)) {
            return true;
        }
        String stripped = trimmed.replaceAll("[\\[\\]{}\",\\s]", "");
        return stripped.isEmpty();
    }

    private List<String> readStringList(JsonNode node) {
        if (node == null) {
            return List.of();
        }
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            for (JsonNode element : node) {
                if (element != null && !element.isNull() && StringUtils.hasText(element.asText())) {
                    values.add(element.asText().trim());
                }
            }
            return values;
        }
        if (node.isTextual()) {
            return splitSimpleList(node.asText());
        }
        return List.of(node.asText());
    }

    private LocalDate parseDate(String value, String field, DeveloperProfileRowParseResult result) {
        if (isBlankish(value)) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        result.addError(field + " has invalid date: " + value);
        return null;
    }
}










