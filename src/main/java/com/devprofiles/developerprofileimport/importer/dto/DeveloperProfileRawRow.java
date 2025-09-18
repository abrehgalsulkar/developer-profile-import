package com.devprofiles.developerprofileimport.importer.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeveloperProfileRawRow {

    private final int rowNumber;
    private final Map<String, String> values;

    public DeveloperProfileRawRow(int rowNumber, Map<String, String> values) {
        this.rowNumber = rowNumber;
        this.values = new LinkedHashMap<>(values);
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public String getValue(String key) {
        return values.get(key);
    }
}
