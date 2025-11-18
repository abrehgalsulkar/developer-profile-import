package com.devprofiles.developerprofileimport.repository;

import com.devprofiles.developerprofileimport.domain.AdminUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

  Optional<AdminUser> findByUsername(String username);
}
