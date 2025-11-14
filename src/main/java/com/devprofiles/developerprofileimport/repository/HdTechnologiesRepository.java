package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdTechnologies;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdTechnologiesRepository extends JpaRepository<HdTechnologies, Long> {
    Optional<HdTechnologies> findByTechnologyIgnoreCase(String technology);
    List<HdTechnologies> findAllByIsActiveTrueAndIsDeletedFalseOrderByTechnologyAsc();
}
