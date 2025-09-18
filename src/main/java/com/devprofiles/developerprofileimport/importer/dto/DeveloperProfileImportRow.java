package com.devprofiles.developerprofileimport.importer.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DeveloperProfileImportRow {

    private int rowNumber;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String introductionVideoUrl;
    private String resumeUrl;
    private Boolean verified;
    private String designation;
    private String jobTitle;
    private Double hourlyRate;
    private String about;
    private Long experienceYears;
    private Long totalWorkedHours;
    private Long totalProjectCompletion;
    private List<String> availabilities = new ArrayList<>();
    private List<String> workLocations = new ArrayList<>();
    private List<String> skills = new ArrayList<>();
    private List<LanguageEntry> languages = new ArrayList<>();
    private String permanentAddress;
    private String temporaryAddress;
    private List<ExperienceEntry> experiences = new ArrayList<>();
    private List<ProjectEntry> projects = new ArrayList<>();
    private List<EducationEntry> educations = new ArrayList<>();
    private List<CertificationEntry> certifications = new ArrayList<>();
    private List<ReviewEntry> reviews = new ArrayList<>();
    private List<FamilyMemberEntry> familyMembers = new ArrayList<>();

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getIntroductionVideoUrl() {
        return introductionVideoUrl;
    }

    public void setIntroductionVideoUrl(String introductionVideoUrl) {
        this.introductionVideoUrl = introductionVideoUrl;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Long experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Long getTotalWorkedHours() {
        return totalWorkedHours;
    }

    public void setTotalWorkedHours(Long totalWorkedHours) {
        this.totalWorkedHours = totalWorkedHours;
    }

    public Long getTotalProjectCompletion() {
        return totalProjectCompletion;
    }

    public void setTotalProjectCompletion(Long totalProjectCompletion) {
        this.totalProjectCompletion = totalProjectCompletion;
    }

    public List<String> getAvailabilities() {
        return Collections.unmodifiableList(availabilities);
    }

    public void setAvailabilities(List<String> availabilities) {
        this.availabilities = new ArrayList<>(Objects.requireNonNullElse(availabilities, List.of()));
    }

    public List<String> getWorkLocations() {
        return Collections.unmodifiableList(workLocations);
    }

    public void setWorkLocations(List<String> workLocations) {
        this.workLocations = new ArrayList<>(Objects.requireNonNullElse(workLocations, List.of()));
    }

    public List<String> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public void setSkills(List<String> skills) {
        this.skills = new ArrayList<>(Objects.requireNonNullElse(skills, List.of()));
    }

    public List<LanguageEntry> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public void setLanguages(List<LanguageEntry> languages) {
        this.languages = new ArrayList<>(Objects.requireNonNullElse(languages, List.of()));
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getTemporaryAddress() {
        return temporaryAddress;
    }

    public void setTemporaryAddress(String temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public List<ExperienceEntry> getExperiences() {
        return Collections.unmodifiableList(experiences);
    }

    public void setExperiences(List<ExperienceEntry> experiences) {
        this.experiences = new ArrayList<>(Objects.requireNonNullElse(experiences, List.of()));
    }

    public List<ProjectEntry> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public void setProjects(List<ProjectEntry> projects) {
        this.projects = new ArrayList<>(Objects.requireNonNullElse(projects, List.of()));
    }

    public List<EducationEntry> getEducations() {
        return Collections.unmodifiableList(educations);
    }

    public void setEducations(List<EducationEntry> educations) {
        this.educations = new ArrayList<>(Objects.requireNonNullElse(educations, List.of()));
    }

    public List<CertificationEntry> getCertifications() {
        return Collections.unmodifiableList(certifications);
    }

    public void setCertifications(List<CertificationEntry> certifications) {
        this.certifications = new ArrayList<>(Objects.requireNonNullElse(certifications, List.of()));
    }

    public List<ReviewEntry> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public void setReviews(List<ReviewEntry> reviews) {
        this.reviews = new ArrayList<>(Objects.requireNonNullElse(reviews, List.of()));
    }

    public List<FamilyMemberEntry> getFamilyMembers() {
        return Collections.unmodifiableList(familyMembers);
    }

    public void setFamilyMembers(List<FamilyMemberEntry> familyMembers) {
        this.familyMembers = new ArrayList<>(Objects.requireNonNullElse(familyMembers, List.of()));
    }

    public static class LanguageEntry {
        private String language;
        private String proficiency;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getProficiency() {
            return proficiency;
        }

        public void setProficiency(String proficiency) {
            this.proficiency = proficiency;
        }
    }

    public static class ExperienceEntry {
        private String companyName;
        private String designation;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> technologies = new ArrayList<>();
        private String responsibilities;
        private String companyLogo;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public List<String> getTechnologies() {
            return Collections.unmodifiableList(technologies);
        }

        public void setTechnologies(List<String> technologies) {
            this.technologies = new ArrayList<>(Objects.requireNonNullElse(technologies, List.of()));
        }

        public String getResponsibilities() {
            return responsibilities;
        }

        public void setResponsibilities(String responsibilities) {
            this.responsibilities = responsibilities;
        }

        public String getCompanyLogo() {
            return companyLogo;
        }

        public void setCompanyLogo(String companyLogo) {
            this.companyLogo = companyLogo;
        }
    }

    public static class ProjectEntry {
        private String projectName;
        private String developerRole;
        private Integer durationInMonths;
        private List<String> technologies = new ArrayList<>();
        private String responsibilities;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getDeveloperRole() {
            return developerRole;
        }

        public void setDeveloperRole(String developerRole) {
            this.developerRole = developerRole;
        }

        public Integer getDurationInMonths() {
            return durationInMonths;
        }

        public void setDurationInMonths(Integer durationInMonths) {
            this.durationInMonths = durationInMonths;
        }

        public List<String> getTechnologies() {
            return Collections.unmodifiableList(technologies);
        }

        public void setTechnologies(List<String> technologies) {
            this.technologies = new ArrayList<>(Objects.requireNonNullElse(technologies, List.of()));
        }

        public String getResponsibilities() {
            return responsibilities;
        }

        public void setResponsibilities(String responsibilities) {
            this.responsibilities = responsibilities;
        }
    }

    public static class EducationEntry {
        private String degreeName;
        private String completionYear;
        private String institution;
        private String location;

        public String getDegreeName() {
            return degreeName;
        }

        public void setDegreeName(String degreeName) {
            this.degreeName = degreeName;
        }

        public String getCompletionYear() {
            return completionYear;
        }

        public void setCompletionYear(String completionYear) {
            this.completionYear = completionYear;
        }

        public String getInstitution() {
            return institution;
        }

        public void setInstitution(String institution) {
            this.institution = institution;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class CertificationEntry {
        private String institutionLogo;
        private String certificationName;
        private String completionYear;

        public String getInstitutionLogo() {
            return institutionLogo;
        }

        public void setInstitutionLogo(String institutionLogo) {
            this.institutionLogo = institutionLogo;
        }

        public String getCertificationName() {
            return certificationName;
        }

        public void setCertificationName(String certificationName) {
            this.certificationName = certificationName;
        }

        public String getCompletionYear() {
            return completionYear;
        }

        public void setCompletionYear(String completionYear) {
            this.completionYear = completionYear;
        }
    }

    public static class ReviewEntry {
        private String reviewerProfilePicture;
        private String reviewerFullName;
        private String designation;
        private Double rating;
        private String review;

        public String getReviewerProfilePicture() {
            return reviewerProfilePicture;
        }

        public void setReviewerProfilePicture(String reviewerProfilePicture) {
            this.reviewerProfilePicture = reviewerProfilePicture;
        }

        public String getReviewerFullName() {
            return reviewerFullName;
        }

        public void setReviewerFullName(String reviewerFullName) {
            this.reviewerFullName = reviewerFullName;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }
    }

    public static class FamilyMemberEntry {
        private String fullName;
        private String relation;
        private String contactNumber;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }
    }
}
