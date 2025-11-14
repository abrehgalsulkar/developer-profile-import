package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdLanguages;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdLanguagesRepository extends JpaRepository<HdLanguages, Long> {
    Optional<HdLanguages> findByNameIgnoreCase(String name);
    List<HdLanguages> findAllByIsActiveTrueAndIsDeletedFalseOrderByNameAsc();
}



