package com.devprofiles.developerprofileimport.importer.service;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProfileRepository;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class DuplicateDetectionService {

    private final HdDeveloperProfileRepository developerProfileRepository;

    public DuplicateDetectionService(HdDeveloperProfileRepository developerProfileRepository) {
        this.developerProfileRepository = developerProfileRepository;
    }

    public Session startSession() {
        return new Session();
    }

    public class Session {
        private final Set<String> inFileKeys = new HashSet<>();
        private final Set<String> knownExistingKeys = new HashSet<>();
        private final Set<String> checkedKeys = new HashSet<>();

        @Transactional(readOnly = true)
        public Optional<String> detectDuplicate(DeveloperProfileImportRow row) {
            String firstName = row.getFirstName();
            String lastName = row.getLastName();
            String designation = row.getDesignation();
            if (!StringUtils.hasText(firstName) || !StringUtils.hasText(lastName)) {
                return Optional.empty();
            }
            String key = buildKey(firstName, lastName, designation);
            if (!inFileKeys.add(key)) {
                return Optional.of("Duplicate row in import file for " + formatName(firstName, lastName, designation));
            }
            if (knownExistingKeys.contains(key)) {
                return Optional.of("Developer already exists for " + formatName(firstName, lastName, designation));
            }
            if (checkedKeys.add(key)) {
                boolean exists = developerProfileRepository.findExistingMatch(firstName, lastName, designation).isPresent();
                if (exists) {
                    knownExistingKeys.add(key);
                    return Optional.of("Developer already exists for " + formatName(firstName, lastName, designation));
                }
            }
            return Optional.empty();
        }

        private String buildKey(String firstName, String lastName, String designation) {
            return normalize(firstName) + "|" + normalize(lastName) + "|" + normalize(designation);
        }

        private String normalize(String value) {
            return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ENGLISH) : "";
        }

        private String formatName(String firstName, String lastName, String designation) {
            StringBuilder builder = new StringBuilder(firstName).append(' ').append(lastName);
            if (StringUtils.hasText(designation)) {
                builder.append(" (").append(designation.trim()).append(')');
            }
            return builder.toString();
        }
    }
}
