package it.moneyverse.user.services;

import static it.moneyverse.user.utils.UserUtils.ONBOARD;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.user.enums.PreferenceKeyEnum;
import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import it.moneyverse.user.model.entities.Preference;
import it.moneyverse.user.model.repositories.PreferenceRepository;
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
  private final PreferenceRepository preferenceRepository;
  private final CurrencyServiceClient currencyServiceClient;

  public UserManagementService(
      KeycloakService keycloakService,
      PreferenceRepository preferenceRepository,
      CurrencyServiceClient currencyServiceClient) {
    this.keycloakService = keycloakService;
    this.preferenceRepository = preferenceRepository;
    this.currencyServiceClient = currencyServiceClient;
  }

  @Override
  @Transactional
  public PreferenceDto createPreferences(UUID userId, List<PreferenceRequest> request) {
    keycloakService
        .getUserById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with id %s not found.".formatted(userId)));
    LOGGER.info("Creating preferences for user with id {}", userId);
    List<Preference> preferences = PreferenceMapper.toPreference(userId, request);
    preferences.forEach(
        preference -> {
          if (PreferenceKeyEnum.CURRENCY.equals(preference.getKey())) {
            checkIfCurrencyExists(preference.getValue());
            preference.setUpdatable(false);
          }
        });
    PreferenceDto result =
        PreferenceMapper.toPreferenceDto(userId, preferenceRepository.saveAll(preferences));
    if (keycloakService.getUserAttributeValue(userId, ONBOARD).isEmpty()) {
      keycloakService.setUserAttribute(userId, ONBOARD, Boolean.TRUE.toString());
    }
    return result;
  }

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }
}
