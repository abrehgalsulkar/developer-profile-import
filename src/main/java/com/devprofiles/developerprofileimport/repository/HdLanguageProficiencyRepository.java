package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdLanguageProficiency;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdLanguageProficiencyRepository extends JpaRepository<HdLanguageProficiency, Long> {
    Optional<HdLanguageProficiency> findByProficiencyLabelIgnoreCase(String proficiencyLabel);
}

