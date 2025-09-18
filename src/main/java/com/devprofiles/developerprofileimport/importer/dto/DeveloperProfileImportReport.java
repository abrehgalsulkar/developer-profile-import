package com.devprofiles.developerprofileimport.importer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeveloperProfileImportReport {

    private int totalRows;
    private int completedRows;
    private int duplicateRows;
    private int incompleteRows;
    private final List<RowIssueDetail> duplicateRowDetails = new ArrayList<>();
    private final List<RowIssueDetail> incompleteRowDetails = new ArrayList<>();

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getCompletedRows() {
        return completedRows;
    }

    public void setCompletedRows(int completedRows) {
        this.completedRows = completedRows;
    }

    public int getDuplicateRows() {
        return duplicateRows;
    }

    public void setDuplicateRows(int duplicateRows) {
        this.duplicateRows = duplicateRows;
    }

    public int getIncompleteRows() {
        return incompleteRows;
    }

    public void setIncompleteRows(int incompleteRows) {
        this.incompleteRows = incompleteRows;
    }

    public List<RowIssueDetail> getDuplicateRowDetails() {
        return Collections.unmodifiableList(duplicateRowDetails);
    }

    public List<RowIssueDetail> getIncompleteRowDetails() {
        return Collections.unmodifiableList(incompleteRowDetails);
    }

    public void addDuplicateRowDetail(RowIssueDetail detail) {
        duplicateRowDetails.add(detail);
    }

    public void addIncompleteRowDetail(RowIssueDetail detail) {
        incompleteRowDetails.add(detail);
    }
}
