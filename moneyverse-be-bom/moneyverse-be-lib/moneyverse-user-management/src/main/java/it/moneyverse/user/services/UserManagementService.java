package it.moneyverse.user.services;

import static it.moneyverse.core.utils.constants.CacheConstants.USERS_CACHE;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.events.UserEvent;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.user.model.dto.LanguageDto;
import it.moneyverse.user.model.dto.UserUpdateRequestDto;
import it.moneyverse.user.model.repositories.LanguageRepository;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);
  private final KeycloakService keycloakService;
  private final MessageProducer<UUID, String> messageProducer;
  private final UserPreferenceRepository userPreferenceRepository;
  private final LanguageRepository languageRepository;

  public UserManagementService(
      KeycloakService keycloakService,
      MessageProducer<UUID, String> messageProducer,
      UserPreferenceRepository userPreferenceRepository,
      LanguageRepository languageRepository) {
    this.keycloakService = keycloakService;
    this.messageProducer = messageProducer;
    this.userPreferenceRepository = userPreferenceRepository;
    this.languageRepository = languageRepository;
  }

  @Override
  public UserDto getUser(UUID userId) {
    return keycloakService
        .getUserById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with id %s not found.".formatted(userId)));
  }

  @Override
  @CachePut(value = USERS_CACHE, key = "#userId", unless = "#result == null")
  public UserDto updateUser(UUID userId, UserUpdateRequestDto request) {
    return keycloakService.updateUser(userId, request);
  }

  @Override
  @CacheEvict(value = USERS_CACHE, key = "#userId")
  public void disableUser(UUID userId) {
    keycloakService.disableUser(userId);
  }

  @Override
  @CacheEvict(value = USERS_CACHE, key = "#userId")
  @Transactional
  public void deleteUser(UUID userId) {
    keycloakService.deleteUser(userId);
    userPreferenceRepository.deleteUserPreferenceByUserId(userId);
    messageProducer.send(UserEvent.builder().withUserId(userId).build(), UserDeletionTopic.TOPIC);
  }

  @Override
  public void checkIfUserExists(UUID userId) {
    getUser(userId);
  }

  @Override
  public List<LanguageDto> getLanguages() {
    LOGGER.info("Getting application languages");
    return languageRepository.findAll().stream()
        .map(
            language ->
                LanguageDto.builder()
                    .withLanguageId(language.getLanguageId())
                    .withIsoCode(language.getIsoCode())
                    .withLocale(language.getLocale())
                    .withCountry(language.getCountry())
                    .withIcon(language.getIcon())
                    .withDefault(language.isDefault())
                    .withEnabled(language.isEnabled())
                    .build())
        .toList();
  }
}
