package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdDesignations;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdDesignationsRepository extends JpaRepository<HdDesignations, Long> {
    Optional<HdDesignations> findByDesignationIgnoreCase(String designation);
}

