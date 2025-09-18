package com.devprofiles.developerprofileimport.importer.parser;

import org.springframework.stereotype.Component;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Component
public class CsvDeveloperProfileFileParser implements DeveloperProfileFileParser {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setTrim(true)
        .setIgnoreEmptyLines(true)
        .build();

    private final Charset charset;

    public CsvDeveloperProfileFileParser() {
        this(StandardCharsets.UTF_8);
    }

    public CsvDeveloperProfileFileParser(Charset charset) {
        this.charset = charset;
    }

    @Override
    public boolean supports(String filename) {
        if (filename == null) {
            return false;
        }
        String lower = filename.toLowerCase(Locale.ENGLISH);
        return lower.endsWith(".csv");
    }

    @Override
    public List<DeveloperProfileRawRow> parse(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, charset);
             CSVParser parser = CSV_FORMAT.parse(reader)) {
            List<DeveloperProfileRawRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                Map<String, String> values = new LinkedHashMap<>();
                record.toMap().forEach((key, value) -> values.put(key, value != null ? value.trim() : null));
                rows.add(new DeveloperProfileRawRow((int) record.getRecordNumber(), values));
            }
            return rows;
        }
    }
}
