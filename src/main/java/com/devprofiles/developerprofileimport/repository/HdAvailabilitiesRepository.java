package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdAvailabilities;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdAvailabilitiesRepository extends JpaRepository<HdAvailabilities, Long> {
    Optional<HdAvailabilities> findByAvailabilityLabelIgnoreCase(String availabilityLabel);
    List<HdAvailabilities> findAllByIsActiveTrueAndIsDeletedFalseOrderByAvailabilityLabelAsc();
}
