package it.moneyverse.core.utils;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.auth.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {

  private SecurityContextUtils() {}

  public static AuthenticatedUser getAuthenticatedUser() {
    return (AuthenticatedUser)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static Boolean isCurrentUserAdmin() {
    return getAuthenticatedUser().getAuthorities().stream()
        .anyMatch(role -> role.getAuthority().equals("ROLE_" + UserRoleEnum.ADMIN.name()));
  }
}
