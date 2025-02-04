package it.moneyverse.core.services;

import it.moneyverse.core.utils.SecurityContextUtils;
import java.util.UUID;

public class SecurityService {

  public boolean isAuthenticatedUserOwner(UUID userId) {
    return userId.equals(SecurityContextUtils.getAuthenticatedUser().getId());
  }

  public UUID getAuthenticatedUserId() {
    return SecurityContextUtils.getAuthenticatedUser().getId();
  }
}
