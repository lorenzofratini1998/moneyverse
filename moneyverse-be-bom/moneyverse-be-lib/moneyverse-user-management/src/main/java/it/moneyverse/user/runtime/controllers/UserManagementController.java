package it.moneyverse.user.runtime.controllers;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import it.moneyverse.user.services.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class UserManagementController implements UserOperations {

  private final UserService userService;

  public UserManagementController(UserService userService) {
    this.userService = userService;
  }

  @Override
  @PostMapping("/users/{userId}/preferences")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public PreferenceDto createPreferences(
      @PathVariable UUID userId, @RequestBody List<PreferenceRequest> request) {
    return userService.createPreferences(userId, request);
  }
}
