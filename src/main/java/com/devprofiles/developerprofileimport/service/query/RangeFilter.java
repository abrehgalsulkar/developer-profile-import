package com.devprofiles.developerprofileimport.service.query;

public record RangeFilter(Long min, Long max) {

    public boolean hasMin() {
        return min != null;
    }

    public boolean hasMax() {
        return max != null;
    }
}
