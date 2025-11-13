package com.devprofiles.developerprofileimport.web;

import com.devprofiles.developerprofileimport.domain.HdDeveloperProfile;
import com.devprofiles.developerprofileimport.service.CartService;
import com.devprofiles.developerprofileimport.service.DeveloperDirectoryService;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CartController {

  private final CartService cartService;
  private final DeveloperDirectoryService developerDirectoryService;

  public CartController(CartService cartService,
                        DeveloperDirectoryService developerDirectoryService) {
    this.cartService = cartService;
    this.developerDirectoryService = developerDirectoryService;
  }

  @GetMapping("/cart")
  public String viewCart(HttpSession session, Model model) {
    Set<Long> cartIds = cartService.getSnapshot(session);
    List<HdDeveloperProfile> developers = developerDirectoryService.findProfilesByIds(cartIds);

    Map<Long, HdDeveloperProfile> ordered = new LinkedHashMap<>();
    for (Long id : cartIds) {
      developers.stream()
          .filter(dev -> dev.getId().equals(id))
          .findFirst()
          .ifPresent(dev -> ordered.put(id, dev));
    }

    double totalHourly = ordered.values().stream()
        .map(HdDeveloperProfile::getHourlyRate)
        .filter(rate -> rate != null && rate > 0)
        .mapToDouble(Double::doubleValue)
        .sum();

    model.addAttribute("cartDevelopers", ordered.values());
    model.addAttribute("cartCount", ordered.size());
    model.addAttribute("totalHourlyRate", totalHourly);
    return "cart";
  }

  @GetMapping("/cart/add")
  public String addDeveloper(@RequestParam("developerId") Long developerId,
                             @RequestHeader(value = "Referer", required = false) String referer,
                             HttpSession session) {
    try {
      developerDirectoryService.findProfileById(developerId);
      cartService.addDeveloper(developerId, session);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer not found");
    }
    return "redirect:" + resolveRedirect(referer);
  }

  @GetMapping("/cart/remove")
  public String removeDeveloper(@RequestParam("developerId") Long developerId,
                                @RequestHeader(value = "Referer", required = false) String referer,
                                HttpSession session) {
    cartService.removeDeveloper(developerId, session);
    return "redirect:" + resolveRedirect(referer);
  }

  private String resolveRedirect(String referer) {
    if (referer == null || referer.isBlank()) {
      return "/developers";
    }
    if (referer.contains("\n") || referer.contains("\r")) {
      return "/developers";
    }
    return referer;
  }
}
