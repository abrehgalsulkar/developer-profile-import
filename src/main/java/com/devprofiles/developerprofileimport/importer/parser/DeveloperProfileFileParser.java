package com.devprofiles.developerprofileimport.importer.parser;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DeveloperProfileFileParser {

    boolean supports(String filename);

    List<DeveloperProfileRawRow> parse(InputStream inputStream) throws IOException;
}
