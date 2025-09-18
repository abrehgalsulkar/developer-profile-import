package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdRecentlyAddedLabels;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdRecentlyAddedLabelsRepository extends JpaRepository<HdRecentlyAddedLabels, Long> {
    Optional<HdRecentlyAddedLabels> findByRecentlyAddedLabelsIgnoreCase(String label);
}


