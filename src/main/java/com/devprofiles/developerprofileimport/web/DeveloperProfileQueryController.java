package com.devprofiles.developerprofileimport.web;

import com.devprofiles.developerprofileimport.service.metadata.FilterMetadataService;
import com.devprofiles.developerprofileimport.service.query.DeveloperProfileQueryService;
import com.devprofiles.developerprofileimport.service.query.DeveloperSearchCriteria;
import com.devprofiles.developerprofileimport.service.query.RangeFilter;
import com.devprofiles.developerprofileimport.web.dto.DeveloperListResponse;
import com.devprofiles.developerprofileimport.web.dto.DeveloperProfileDto;
import com.devprofiles.developerprofileimport.web.dto.FilterMetadataResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/developer-profiles")
public class DeveloperProfileQueryController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final DeveloperProfileQueryService queryService;
    private final FilterMetadataService filterMetadataService;

    public DeveloperProfileQueryController(DeveloperProfileQueryService queryService,
                                           FilterMetadataService filterMetadataService) {
        this.queryService = queryService;
        this.filterMetadataService = filterMetadataService;
    }

    @GetMapping("/filters")
    public FilterMetadataResponse filters() {
        return filterMetadataService.getMetadata();
    }

    @GetMapping
    public DeveloperListResponse listDevelopers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "technology", required = false) List<String> technologies,
            @RequestParam(value = "experienceRange", required = false) List<String> experienceRanges,
            @RequestParam(value = "projectCompletionRange", required = false) List<String> projectCompletionRanges,
            @RequestParam(value = "workLocation", required = false) List<String> workLocations,
            @RequestParam(value = "availability", required = false) List<String> availabilities,
            @RequestParam(value = "language", required = false) List<String> languages,
            @RequestParam(value = "languageProficiency", required = false) String languageProficiency,
            @RequestParam(value = "hourlyMin", required = false) Double hourlyMin,
            @RequestParam(value = "hourlyMax", required = false) Double hourlyMax,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "projects") String sortField,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String sortDirection) {

        DeveloperSearchCriteria criteria = new DeveloperSearchCriteria(
                trim(keyword),
                normalizeList(technologies),
                parseRanges(experienceRanges),
                parseRanges(projectCompletionRanges),
                normalizeList(workLocations),
                normalizeList(availabilities),
                normalizeList(languages),
                trimToLower(languageProficiency),
                hourlyMin,
                hourlyMax);

        Pageable pageable = PageRequest.of(
                sanitizePage(page),
                sanitizeSize(size),
                resolveSort(sortField, sortDirection));

        Page<DeveloperProfileDto> pageResult = queryService.search(criteria, pageable);

        return new DeveloperListResponse(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                pageResult.getNumber(),
                pageResult.getSize());
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ENGLISH))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<RangeFilter> parseRanges(List<String> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            return List.of();
        }
        List<RangeFilter> result = new ArrayList<>();
        for (String raw : ranges) {
            if (raw == null) {
                continue;
            }
            String value = raw.trim();
            if (value.isEmpty()) {
                continue;
            }
            if (value.endsWith("+")) {
                parseLong(value.substring(0, value.length() - 1))
                        .ifPresent(min -> result.add(new RangeFilter(min, null)));
            } else if (value.contains("-")) {
                String[] parts = value.split("-", 2);
                Optional<Long> min = parseLong(parts[0]);
                Optional<Long> max = parts.length > 1 ? parseLong(parts[1]) : Optional.empty();
                if (min.isPresent() || max.isPresent()) {
                    result.add(new RangeFilter(min.orElse(null), max.orElse(null)));
                }
            } else {
                parseLong(value).ifPresent(number -> result.add(new RangeFilter(number, number)));
            }
        }
        return result;
    }

    private Optional<Long> parseLong(String value) {
        try {
            return Optional.of(Long.parseLong(value.trim()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Sort resolveSort(String sortField, String direction) {
        String key = Optional.ofNullable(sortField)
                .map(String::toLowerCase)
                .orElse("projects");
        String property = switch (key) {
            case "experience" -> "numberOfExperience";
            case "hourlyrate" -> "hourlyRate";
            case "verified" -> "isVerified";
            case "projects" -> "totalProjectCompletion";
            default -> "totalProjectCompletion";
        };

        String normalizedDirection = Optional.ofNullable(direction)
                .map(String::toLowerCase)
                .orElse("desc");

        if ("default".equals(normalizedDirection)) {
            return Sort.by(Sort.Direction.DESC, property);
        }

        Direction sortDirection = Direction.fromOptionalString(normalizedDirection)
                .orElse(Direction.DESC);
        return Sort.by(sortDirection, property);
    }

    private int sanitizePage(Integer page) {
        int value = page == null ? DEFAULT_PAGE : page;
        return Math.max(0, value);
    }

    private int sanitizeSize(Integer size) {
        int value = size == null ? DEFAULT_SIZE : size;
        if (value < 1) {
            value = DEFAULT_SIZE;
        }
        return Math.min(value, MAX_PAGE_SIZE);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToLower(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.ENGLISH);
    }
}
