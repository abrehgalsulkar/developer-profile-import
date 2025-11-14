package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdExperienceRanges;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdExperienceRangesRepository extends JpaRepository<HdExperienceRanges, Long> {
    Optional<HdExperienceRanges> findByExperienceRangeLabelIgnoreCase(String experienceRange);
    List<HdExperienceRanges> findAllByIsActiveTrueAndIsDeletedFalseOrderByMinimumExperienceAsc();
}


