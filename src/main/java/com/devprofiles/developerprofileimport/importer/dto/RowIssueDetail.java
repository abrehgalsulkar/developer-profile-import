package com.devprofiles.developerprofileimport.importer.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RowIssueDetail {

    private final int rowNumber;
    private final List<String> messages = new ArrayList<>();

    public RowIssueDetail(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public RowIssueDetail addMessage(String message) {
        if (message != null && !message.isBlank()) {
            messages.add(message);
        }
        return this;
    }
}
