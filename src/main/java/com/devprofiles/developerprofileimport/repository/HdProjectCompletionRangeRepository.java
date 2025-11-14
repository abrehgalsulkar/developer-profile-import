package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdProjectCompletionRange;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdProjectCompletionRangeRepository extends JpaRepository<HdProjectCompletionRange, Long> {
    Optional<HdProjectCompletionRange> findByProjectCompletionRangeLabelIgnoreCase(String projectCompletionRange);
    List<HdProjectCompletionRange> findAllByIsActiveTrueAndIsDeletedFalseOrderByMinProjectsAsc();
}


