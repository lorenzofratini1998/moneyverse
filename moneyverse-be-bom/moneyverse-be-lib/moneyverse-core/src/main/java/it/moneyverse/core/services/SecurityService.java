package it.moneyverse.core.services;

import it.moneyverse.core.utils.SecurityContextUtils;
import java.util.UUID;

public class SecurityService {

  public Boolean isAuthenticatedUser(UUID userId) {
    return userId.equals(UUID.fromString(SecurityContextUtils.getAuthenticatedUser().getId()));
  }
}
