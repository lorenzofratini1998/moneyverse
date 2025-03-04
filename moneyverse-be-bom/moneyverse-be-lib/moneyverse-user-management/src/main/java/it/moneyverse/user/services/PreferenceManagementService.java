package it.moneyverse.user.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PreferenceDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import it.moneyverse.core.services.CurrencyServiceClient;
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
public class PreferenceManagementService implements PreferenceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceManagementService.class);

  private final UserService userService;
  private final CurrencyServiceClient currencyServiceClient;

  private final UserPreferenceRepository userPreferenceRepository;
  private final PreferenceRepository preferenceRepository;

  public PreferenceManagementService(
      UserService userService,
      CurrencyServiceClient currencyServiceClient,
      UserPreferenceRepository userPreferenceRepository,
      PreferenceRepository preferenceRepository) {
    this.userService = userService;
    this.currencyServiceClient = currencyServiceClient;
    this.userPreferenceRepository = userPreferenceRepository;
    this.preferenceRepository = preferenceRepository;
  }

  @Override
  @Transactional
  public List<UserPreferenceDto> createUserPreferences(
      UUID userId, List<UserPreferenceRequest> request) {
    userService.checkIfUserExists(userId);
    LOGGER.info("Creating preferences for user with id {}", userId);

    List<UserPreference> userPreferences =
        request.stream()
            .map(userPreferenceRequest -> createUserPreference(userId, userPreferenceRequest))
            .toList();

    return PreferenceMapper.toUserPreferenceDto(userPreferenceRepository.saveAll(userPreferences));
  }

  private UserPreference createUserPreference(UUID userId, UserPreferenceRequest request) {
    Preference preference = getPreferenceById(request.preferenceId());
    if (preference.getName().equalsIgnoreCase("CURRENCY")) {
      currencyServiceClient.checkIfCurrencyExists(request.value());
    }
    return PreferenceMapper.toUserPreference(userId, request, preference);
  }

  private Preference getPreferenceById(UUID preferenceId) {
    return preferenceRepository
        .findById(preferenceId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Preference with id %s not found.".formatted(preferenceId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserPreferenceDto> getUserPreferences(UUID userId, Boolean mandatory) {
    return mandatory == null ? getUserPreferences(userId) : getUserMandatoryPreferences(userId);
  }

  private List<UserPreferenceDto> getUserPreferences(UUID userId) {
    LOGGER.info("Getting preferences for user with id {}", userId);
    return PreferenceMapper.toUserPreferenceDto(userPreferenceRepository.findByUserId(userId));
  }

  private List<UserPreferenceDto> getUserMandatoryPreferences(UUID userId) {
    LOGGER.info("Getting mandatory preferences for user with id {}", userId);
    return PreferenceMapper.toUserPreferenceDto(
        userPreferenceRepository.findMandatoryPreferencesByUserId(userId));
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
