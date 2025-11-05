package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HdDeveloperProfileRepository extends JpaRepository<HdDeveloperProfile, Long> {

    @Query("""
        select p from HdDeveloperProfile p
        left join p.designation d
        where lower(p.firstName) = lower(:firstName)
          and lower(p.lastName) = lower(:lastName)
          and (
                (:designation is null and d is null)
             or (d is not null and lower(d.designation) = lower(:designation))
          )
    """)
    Optional<HdDeveloperProfile> findExistingMatch(@Param("firstName") String firstName,
                                                   @Param("lastName") String lastName,
                                                   @Param("designation") String designation);

    Optional<HdDeveloperProfile> findByEmailIgnoreCase(String email);

    Optional<HdDeveloperProfile> findByPhoneNumber(String phoneNumber);
}
