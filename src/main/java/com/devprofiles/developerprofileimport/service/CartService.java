package com.devprofiles.developerprofileimport.service;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CartService {

  private static final String CART_SESSION_KEY = "developerCart";

  @SuppressWarnings("unchecked")
  private Set<Long> getOrCreateCart(HttpSession session) {
    Object attribute = session.getAttribute(CART_SESSION_KEY);
    if (attribute instanceof Set) {
      return (Set<Long>) attribute;
    }
    Set<Long> cart = new LinkedHashSet<>();
    session.setAttribute(CART_SESSION_KEY, cart);
    return cart;
  }

  public Set<Long> getSnapshot(HttpSession session) {
    return Collections.unmodifiableSet(new LinkedHashSet<>(getOrCreateCart(session)));
  }

  public void addDeveloper(Long developerId, HttpSession session) {
    getOrCreateCart(session).add(developerId);
  }

  public void removeDeveloper(Long developerId, HttpSession session) {
    getOrCreateCart(session).remove(developerId);
  }

  public boolean isInCart(Long developerId, HttpSession session) {
    return getOrCreateCart(session).contains(developerId);
  }
}
