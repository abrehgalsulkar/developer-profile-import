package com.devprofiles.developerprofileimport.importer.parser;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class DeveloperProfileFileParserResolver {

    private final List<DeveloperProfileFileParser> delegates;

    public DeveloperProfileFileParserResolver(List<DeveloperProfileFileParser> delegates) {
        this.delegates = List.copyOf(delegates);
    }

    public List<DeveloperProfileRawRow> parse(String filename, InputStream inputStream) throws IOException {
        DeveloperProfileFileParser parser = resolve(filename);
        return parser.parse(inputStream);
    }

    private DeveloperProfileFileParser resolve(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename must be provided for parser resolution");
        }
        String cleaned = filename.trim();
        return delegates.stream()
            .filter(parser -> parser.supports(cleaned))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + cleaned.toLowerCase(Locale.ENGLISH)));
    }
}
