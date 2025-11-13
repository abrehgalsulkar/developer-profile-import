package com.devprofiles.developerprofileimport.web;

import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.service.CartService;
import com.devprofiles.developerprofileimport.service.DeveloperDirectoryService;
import com.devprofiles.developerprofileimport.service.dto.DeveloperFilterCriteria;
import com.devprofiles.developerprofileimport.service.dto.DeveloperSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

@Controller
public class DeveloperDirectoryController {

  private static final Logger log = LoggerFactory.getLogger(DeveloperDirectoryController.class);

  private final DeveloperDirectoryService developerDirectoryService;
  private final CartService cartService;

  public DeveloperDirectoryController(DeveloperDirectoryService developerDirectoryService,
                                      CartService cartService) {
    this.developerDirectoryService = developerDirectoryService;
    this.cartService = cartService;
  }

  @GetMapping("/developers")
  public String showDevelopers(@ModelAttribute("filters") DeveloperFilterCriteria filters,
                               @RequestParam(name = "sortOption", required = false) String sortOptionParam,
                               @RequestParam(name = "sortDirection", required = false) String sortDirectionParam,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "12") int size,
                               Model model,
                               HttpSession session) {

    if (sortOptionParam != null && !sortOptionParam.isBlank()) {
      try {
        filters.setSortOption(DeveloperSortOption.valueOf(sortOptionParam));
      } catch (IllegalArgumentException ex) {
        log.warn("Unknown sort option '{}', falling back to default.", sortOptionParam);
        filters.setSortOption(DeveloperSortOption.DEFAULT);
      }
    }

    if (sortDirectionParam != null && !sortDirectionParam.isBlank()) {
      try {
        filters.setSortDirection(Sort.Direction.fromString(sortDirectionParam));
      } catch (IllegalArgumentException ex) {
        log.warn("Unknown sort direction '{}', falling back to DESC.", sortDirectionParam);
        filters.setSortDirection(Sort.Direction.DESC);
      }
    }

    filters.setPage(page);
    filters.setSize(size);

    log.debug("Listing developers with filters: sortOption={}, sortDirection={}, page={}, size={}",
        filters.getSortOption(), filters.getSortDirection(), filters.getPage(), filters.getSize());

    Page<HdDeveloperProfile> results = developerDirectoryService.findProfiles(filters);
    var cartIds = cartService.getSnapshot(session);
    model.addAttribute("developersPage", results);
    model.addAttribute("developers", results.getContent());
    model.addAttribute("developerCount", results.getTotalElements());
    model.addAttribute("cartDeveloperIds", cartIds);
    model.addAttribute("cartCount", cartIds.size());
    return "developers";
  }

  @GetMapping("/developers/{developerId}")
  public String showDeveloperDetail(@PathVariable Long developerId, Model model, HttpSession session) {
    try {
      HdDeveloperProfile developer = developerDirectoryService.findProfileById(developerId);
      var cartIds = cartService.getSnapshot(session);
      model.addAttribute("developer", developer);
      model.addAttribute("inCart", cartIds.contains(developerId));
      model.addAttribute("cartCount", cartIds.size());
      return "developer-detail";
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer not found");
    }
  }

  @GetMapping("/")
  public String redirectRoot() {
    return "redirect:/developers";
  }
}
