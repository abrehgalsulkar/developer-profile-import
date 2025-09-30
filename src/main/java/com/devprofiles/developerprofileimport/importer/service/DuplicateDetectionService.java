package com.devprofiles.developerprofileimport.importer.service;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileImportRow;
import com.devprofiles.developerprofileimport.repository.HdDeveloperProfileRepository;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class DuplicateDetectionService {

    private static final Pattern NON_DIGIT = Pattern.compile("\\D");

    private final HdDeveloperProfileRepository developerProfileRepository;

    public DuplicateDetectionService(HdDeveloperProfileRepository developerProfileRepository) {
        this.developerProfileRepository = developerProfileRepository;
    }

    public Session startSession() {
        return new Session();
    }

    public class Session {
        private final Set<String> inFileEmails = new HashSet<>();
        private final Set<String> inFilePhones = new HashSet<>();
        private final Set<String> knownExistingEmails = new HashSet<>();
        private final Set<String> knownExistingPhones = new HashSet<>();
        private final Set<String> checkedEmails = new HashSet<>();
        private final Set<String> checkedPhones = new HashSet<>();

        @Transactional(readOnly = true)
        public Optional<String> detectDuplicate(DeveloperProfileImportRow row) {
            String rawEmail = row.getEmail();
            String rawPhone = row.getPhoneNumber();
            String normalizedEmail = normalizeEmail(rawEmail);
            String normalizedPhone = normalizePhone(rawPhone);

            if (!StringUtils.hasText(normalizedEmail) && !StringUtils.hasText(normalizedPhone)) {
                return Optional.empty();
            }

            if (StringUtils.hasText(normalizedEmail)) {
                if (!inFileEmails.add(normalizedEmail)) {
                    return Optional.of("Duplicate email in import file: " + rawEmail);
                }
                if (knownExistingEmails.contains(normalizedEmail)) {
                    return Optional.of("Developer already exists for email " + rawEmail);
                }
                if (checkedEmails.add(normalizedEmail)) {
                    boolean exists = developerProfileRepository
                            .findByEmailIgnoreCase(normalizedEmail)
                            .isPresent();
                    if (exists) {
                        knownExistingEmails.add(normalizedEmail);
                        return Optional.of("Developer already exists for email " + rawEmail);
                    }
                }
            }

            if (StringUtils.hasText(normalizedPhone)) {
                if (!inFilePhones.add(normalizedPhone)) {
                    return Optional.of("Duplicate phone number in import file: " + rawPhone);
                }
                if (knownExistingPhones.contains(normalizedPhone)) {
                    return Optional.of("Developer already exists for phone number " + rawPhone);
                }
                if (checkedPhones.add(normalizedPhone)) {
                    boolean exists = developerProfileRepository
                            .findByPhoneNumber(normalizedPhone)
                            .isPresent();
                    if (exists) {
                        knownExistingPhones.add(normalizedPhone);
                        return Optional.of("Developer already exists for phone number " + rawPhone);
                    }
                }
            }

            return Optional.empty();
        }

        private String normalizeEmail(String value) {
            return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ENGLISH) : null;
        }

        private String normalizePhone(String value) {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            String digits = NON_DIGIT.matcher(value).replaceAll("");
            return digits.isEmpty() ? null : digits;
        }
    }
}
