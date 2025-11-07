package com.devprofiles.developerprofileimport.service;

import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProfileRepository;
import com.devprofiles.developerprofileimport.service.dto.DeveloperFilterCriteria;
import com.devprofiles.developerprofileimport.service.dto.DeveloperSortOption;
import com.devprofiles.developerprofileimport.service.specification.DeveloperProfileSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DeveloperDirectoryService {

  private final HdDeveloperProfileRepository developerProfileRepository;

  public DeveloperDirectoryService(HdDeveloperProfileRepository developerProfileRepository) {
    this.developerProfileRepository = developerProfileRepository;
  }

  public Page<HdDeveloperProfile> findProfiles(DeveloperFilterCriteria criteria) {
    DeveloperFilterCriteria effectiveCriteria = criteria != null ? criteria : new DeveloperFilterCriteria();
    Specification<HdDeveloperProfile> specification = DeveloperProfileSpecifications.withFilter(effectiveCriteria);
    Pageable pageable = PageRequest.of(
        effectiveCriteria.getPage(),
        effectiveCriteria.getSize(),
        resolveSort(effectiveCriteria)
    );
    return developerProfileRepository.findAll(specification, pageable);
  }

  private Sort resolveSort(DeveloperFilterCriteria criteria) {
    Sort.Direction direction = criteria.getSortDirection();
    DeveloperSortOption sortOption = criteria.getSortOption();

    return switch (sortOption) {
      case PROJECTS ->
          Sort.by(direction, "totalProjectCompletion", "updatedAt", "id");
      case EXPERIENCE ->
          Sort.by(direction, "numberOfExperience", "updatedAt", "id");
      case HOURLY_RATE ->
          Sort.by(direction, "hourlyRate", "updatedAt", "id");
      case VERIFIED ->
          Sort.by(direction, "isVerified", "updatedAt", "id");
      case DEFAULT ->
          Sort.by(direction, "updatedAt", "id");
    };
  }
}
