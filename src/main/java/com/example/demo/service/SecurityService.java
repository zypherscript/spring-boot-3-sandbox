package com.example.demo.service;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("vaadin-sec-view")
public class SecurityService {

  private final AuthenticationContext authenticationContext;

  public UserDetails getAuthenticatedUser() {
    return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
  }

  public void logout() {
    authenticationContext.logout();
  }
}
