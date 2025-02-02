package it.moneyverse.user.runtime.controllers;

import it.moneyverse.user.model.dto.*;
import it.moneyverse.user.services.PreferenceService;
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
public class UserManagementController implements UserOperations, PreferenceOperations {

  private final PreferenceService preferenceService;
  private final UserService userService;

  public UserManagementController(PreferenceService preferenceService, UserService userService) {
    this.preferenceService = preferenceService;
    this.userService = userService;
  }

  @Override
  @PostMapping("/users/{userId}/preferences")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public UserPreferenceDto createUserPreferences(
      @PathVariable UUID userId, @RequestBody List<UserPreferenceRequest> request) {
    return preferenceService.createUserPreferences(userId, request);
  }

  @Override
  @GetMapping("/users/{userId}/preferences")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public UserPreferenceDto getUserPreferences(
      @PathVariable UUID userId, @RequestParam(required = false) Boolean mandatory) {
    return preferenceService.getUserPreferences(userId, mandatory);
  }

  @Override
  @GetMapping("/preferences")
  @ResponseStatus(HttpStatus.OK)
  public List<PreferenceDto> getPreferences(@RequestParam Boolean mandatory) {
    return preferenceService.getPreferences(mandatory);
  }

  @Override
  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public UserDto getUser(@PathVariable UUID userId) {
    return userService.getUser(userId);
  }

  @Override
  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public UserDto updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequestDto request) {
    return userService.updateUser(userId, request);
  }

  @Override
  @DeleteMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public void deleteUser(@PathVariable UUID userId) {
    userService.deleteUser(userId);
  }

  @Override
  @PatchMapping("/users/{userId}/disable")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "T(it.moneyverse.core.utils.SecurityContextUtils).getAuthenticatedUser().id.equals(#userId.toString())")
  public void disableUser(@PathVariable UUID userId) {
    userService.disableUser(userId);
  }
}
