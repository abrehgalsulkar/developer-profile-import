package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdSkills;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdSkillsRepository extends JpaRepository<HdSkills, Long> {
    Optional<HdSkills> findByNameIgnoreCase(String name);
}



