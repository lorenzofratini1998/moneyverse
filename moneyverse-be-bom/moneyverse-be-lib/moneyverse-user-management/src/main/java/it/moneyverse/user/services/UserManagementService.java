package it.moneyverse.user.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceDto;
import it.moneyverse.user.model.dto.UserPreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.entities.UserPreference;
import it.moneyverse.user.model.repositories.PreferenceRepository;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import it.moneyverse.user.utils.mapper.PreferenceMapper;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

  private final KeycloakService keycloakService;
  private final CurrencyServiceClient currencyServiceClient;

  private final UserPreferenceRepository userPreferenceRepository;
  private final PreferenceRepository preferenceRepository;

  public UserManagementService(
      KeycloakService keycloakService,
      CurrencyServiceClient currencyServiceClient,
      UserPreferenceRepository userPreferenceRepository,
      PreferenceRepository preferenceRepository) {
    this.keycloakService = keycloakService;
    this.currencyServiceClient = currencyServiceClient;
    this.userPreferenceRepository = userPreferenceRepository;
    this.preferenceRepository = preferenceRepository;
  }

  @Override
  @Transactional
  public UserPreferenceDto createUserPreferences(UUID userId, List<UserPreferenceRequest> request) {
    keycloakService
        .getUserById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with id %s not found.".formatted(userId)));
    LOGGER.info("Creating preferences for user with id {}", userId);

    List<UserPreference> userPreferences =
        request.stream()
            .map(
                userPreferenceRequest -> {
                  Preference preference =
                      getPreferenceById(
                          userPreferenceRequest.preferenceId(), userPreferenceRequest.value());
                  return PreferenceMapper.toUserPreference(
                      userId, userPreferenceRequest, preference);
                })
            .toList();

    return PreferenceMapper.toUserPreferenceDto(
        userId, userPreferenceRepository.saveAll(userPreferences));
  }

  private Preference getPreferenceById(UUID preferenceId, String preferenceValue) {
    Preference preference =
        preferenceRepository
            .findById(preferenceId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Preference with id %s not found.".formatted(preferenceId)));
    if (preference.getName().equalsIgnoreCase("CURRENCY")) {
      checkIfCurrencyExists(preferenceValue);
    }
    return preference;
  }

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UserPreferenceDto getUserPreferences(UUID userId, Boolean mandatory) {
    return mandatory == null ? getUserPreferences(userId) : getUserMandatoryPreferences(userId);
  }

  private UserPreferenceDto getUserPreferences(UUID userId) {
    LOGGER.info("Getting preferences for user with id {}", userId);
    return PreferenceMapper.toUserPreferenceDto(
        userId, userPreferenceRepository.findByUserId(userId));
  }

  private UserPreferenceDto getUserMandatoryPreferences(UUID userId) {
    LOGGER.info("Getting mandatory preferences for user with id {}", userId);
    return PreferenceMapper.toUserPreferenceDto(
        userId, userPreferenceRepository.findMandatoryPreferencesByUserId(userId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PreferenceDto> getPreferences(Boolean mandatory) {
    return PreferenceMapper.toPreferenceDto(
        mandatory == null
            ? preferenceRepository.findAll()
            : preferenceRepository.findAllByMandatory(true));
  }
}
