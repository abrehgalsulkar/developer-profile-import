package com.devprofiles.developerprofileimport.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;

public class DeveloperFilterCriteria {

  private String searchTerm;
  private List<String> technologies = new ArrayList<>();
  private List<String> experienceRanges = new ArrayList<>();
  private List<String> projectCompletions = new ArrayList<>();
  private List<String> workLocations = new ArrayList<>();
  private List<String> availabilities = new ArrayList<>();
  private List<String> languages = new ArrayList<>();
  private String languageProficiency;
  private Double minHourlyRate;
  private Double maxHourlyRate;
  private Boolean verified;
  private DeveloperSortOption sortOption = DeveloperSortOption.DEFAULT;
  private Sort.Direction sortDirection = Sort.Direction.DESC;
  private int page = 0;
  private int size = 12;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  public List<String> getTechnologies() {
    return technologies;
  }

  public void setTechnologies(List<String> technologies) {
    this.technologies = sanitizeList(technologies);
  }

  public List<String> getExperienceRanges() {
    return experienceRanges;
  }

  public void setExperienceRanges(List<String> experienceRanges) {
    this.experienceRanges = sanitizeList(experienceRanges);
  }

  public List<String> getProjectCompletions() {
    return projectCompletions;
  }

  public void setProjectCompletions(List<String> projectCompletions) {
    this.projectCompletions = sanitizeList(projectCompletions);
  }

  public List<String> getWorkLocations() {
    return workLocations;
  }

  public void setWorkLocations(List<String> workLocations) {
    this.workLocations = sanitizeList(workLocations);
  }

  public List<String> getAvailabilities() {
    return availabilities;
  }

  public void setAvailabilities(List<String> availabilities) {
    this.availabilities = sanitizeList(availabilities);
  }

  public List<String> getLanguages() {
    return languages;
  }

  public void setLanguages(List<String> languages) {
    this.languages = sanitizeList(languages);
  }

  public String getLanguageProficiency() {
    return languageProficiency;
  }

  public void setLanguageProficiency(String languageProficiency) {
    this.languageProficiency = normalize(languageProficiency);
  }

  public Double getMinHourlyRate() {
    return minHourlyRate;
  }

  public void setMinHourlyRate(Double minHourlyRate) {
    this.minHourlyRate = minHourlyRate;
  }

  public Double getMaxHourlyRate() {
    return maxHourlyRate;
  }

  public void setMaxHourlyRate(Double maxHourlyRate) {
    this.maxHourlyRate = maxHourlyRate;
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public DeveloperSortOption getSortOption() {
    return sortOption;
  }

  public void setSortOption(DeveloperSortOption sortOption) {
    this.sortOption = sortOption == null ? DeveloperSortOption.DEFAULT : sortOption;
  }

  public Sort.Direction getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(Sort.Direction sortDirection) {
    this.sortDirection = sortDirection == null ? Sort.Direction.DESC : sortDirection;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = Math.max(page, 0);
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size > 0 ? size : 12;
  }

  private List<String> sanitizeList(List<String> values) {
    if (values == null || values.isEmpty()) {
      return new ArrayList<>();
    }
    return values.stream()
      .filter(Objects::nonNull)
      .map(this::normalize)
      .filter(s -> !s.isEmpty())
      .toList();
  }

  private String normalize(String input) {
    return input == null ? "" : input.trim();
  }
}
