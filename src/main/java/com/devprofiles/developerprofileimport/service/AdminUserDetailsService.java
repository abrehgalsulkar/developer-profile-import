package com.devprofiles.developerprofileimport.service;

import com.devprofiles.developerprofileimport.domain.AdminUser;
import com.devprofiles.developerprofileimport.repository.AdminUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

  private final AdminUserRepository adminUserRepository;

  public AdminUserDetailsService(AdminUserRepository adminUserRepository) {
    this.adminUserRepository = adminUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return adminUserRepository.findByUsername(username)
        .filter(AdminUser::isEnabled)
        .map(user -> User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(resolveRole(user.getRole()))
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("Admin user '%s' not found".formatted(username)));
  }

  private String resolveRole(String role) {
    if (role == null || role.isBlank()) {
      return "ADMIN";
    }
    return role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role;
  }
}
