package com.devprofiles.developerprofileimport.importer.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelImportPreview {
    public List<DeveloperAggregate> developers = new ArrayList<>();
    public List<RowError> warnings = new ArrayList<>();
    public static class RowError {
        public String sheet;
        public int row;
        public String message;
        public RowError(String sheet, int row, String message) {
            this.sheet = sheet; this.row = row; this.message = message;
        }
    }
    public List<RowError> errors = new ArrayList<>();
}

