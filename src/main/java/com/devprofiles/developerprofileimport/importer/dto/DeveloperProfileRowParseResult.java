package com.devprofiles.developerprofileimport.importer.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DeveloperProfileRowParseResult {

    private DeveloperProfileImportRow row;
    private final List<String> errors = new ArrayList<>();

    public DeveloperProfileRowParseResult row(DeveloperProfileImportRow row) {
        this.row = row;
        return this;
    }

    public DeveloperProfileRowParseResult addError(String message) {
        if (message != null && !message.isBlank()) {
            errors.add(message);
        }
        return this;
    }

    public Optional<DeveloperProfileImportRow> getRow() {
        return Optional.ofNullable(row);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
