package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdDeveloperKnownLanguages;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HdDeveloperKnownLanguagesRepository extends JpaRepository<HdDeveloperKnownLanguages, Long> {

    List<HdDeveloperKnownLanguages> findByHdDeveloperProfileIdIn(Iterable<Long> profileIds);
}

