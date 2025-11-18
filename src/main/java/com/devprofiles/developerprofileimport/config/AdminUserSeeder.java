package com.devprofiles.developerprofileimport.config;

import com.devprofiles.developerprofileimport.domain.AdminUser;
import com.devprofiles.developerprofileimport.repository.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminUserSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final String defaultUsername;
  private final String defaultPassword;

  public AdminUserSeeder(AdminUserRepository adminUserRepository,
                         PasswordEncoder passwordEncoder,
                         @Value("${app.security.admin.default-username:}") String defaultUsername,
                         @Value("${app.security.admin.default-password:}") String defaultPassword) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.defaultUsername = defaultUsername;
    this.defaultPassword = defaultPassword;
  }

  @Override
  public void run(String... args) {
    if (!StringUtils.hasText(defaultUsername) || !StringUtils.hasText(defaultPassword)) {
      return;
    }

    adminUserRepository.findByUsername(defaultUsername)
        .orElseGet(() -> {
          AdminUser admin = new AdminUser();
          admin.setUsername(defaultUsername);
          admin.setPassword(passwordEncoder.encode(defaultPassword));
          admin.setEnabled(true);
          admin.setRole("ADMIN");
          AdminUser saved = adminUserRepository.save(admin);
          return saved;
        });
  }
}
