package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdWorkLocations;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdWorkLocationsRepository extends JpaRepository<HdWorkLocations, Long> {
    Optional<HdWorkLocations> findByWorkLocationLabelIgnoreCase(String workLocation);
    List<HdWorkLocations> findAllByIsActiveTrueAndIsDeletedFalseOrderByWorkLocationLabelAsc();
}


