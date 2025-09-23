package com.devprofiles.developerprofileimport.importer.excel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeveloperAggregate {
    public String developerKey;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Boolean verifiedDeveloper;
    public Boolean availableDeveloper;
    public String designation;
    public String jobTitle;
    public Double hourlyRate;
    public String about;
    public Long experienceYears;
    public Long totalWorkedHours;
    public Long totalProjectCompletion;
    public String profilePictureFile;
    public String introVideoFile;
    public String cvFile;
    public String permanentAddress;
    public String temporaryAddress;

    public List<String> languages = new ArrayList<>();
    public List<String> languageProficiencies = new ArrayList<>();
    public List<String> skills = new ArrayList<>();
    public List<String> availabilities = new ArrayList<>();
    public List<String> workLocations = new ArrayList<>();

    public List<Experience> experiences = new ArrayList<>();
    public List<Project> projects = new ArrayList<>();
    public List<Education> educations = new ArrayList<>();
    public List<Certification> certifications = new ArrayList<>();
    public List<Review> reviews = new ArrayList<>();
    public List<FamilyMember> family = new ArrayList<>();

    public static class Experience {
        public String experienceKey;
        public String companyLogoFile;
        public String companyName;
        public String designation;
        public LocalDate startDate;
        public LocalDate endDate;
        public String responsibilities;
        public List<String> technologies = new ArrayList<>();
    }

    public static class Project {
        public String projectKey;
        public String projectName;
        public String developerRole;
        public Integer durationMonths;
        public String responsibilities;
        public List<String> technologies = new ArrayList<>();
    }

    public static class Education {
        public String degreeName;
        public String completionYear;
        public String institution;
        public String location;
    }

    public static class Certification {
        public String institutionLogoFile;
        public String certificationName;
        public String completionYear;
    }

    public static class Review {
        public String reviewerFullName;
        public Boolean anonymousReview;
        public String reviewerProfilePictureFile;
        public String designation;
        public Double rating;
        public String review;
    }

    public static class FamilyMember {
        public String fullName;
        public String relation;
        public String contactNumber;
    }
}
