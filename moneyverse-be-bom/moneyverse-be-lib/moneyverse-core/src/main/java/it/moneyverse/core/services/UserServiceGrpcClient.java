package it.moneyverse.core.services;

import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.core.model.dto.UserPreferenceDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserServiceGrpcClient implements UserServiceClient {

  private final UserGrpcService userGrpcService;

  public UserServiceGrpcClient(UserGrpcService userGrpcService) {
    this.userGrpcService = userGrpcService;
  }

  @Override
  public Optional<UserDto> getUserById(UUID userId) {
    return userGrpcService.getUserById(userId);
  }

  @Override
  public Optional<UserPreferenceDto> getUserPreference(UUID userId, String preferenceName) {
    return userGrpcService.getUserPreference(userId, preferenceName);
  }

  @Override
  public void checkIfUserStillExist(UUID userId) {
    if (userGrpcService.getUserById(userId).isPresent()) {
      throw new ResourceStillExistsException(
          "User %s still exists in the system".formatted(userId));
    }
  }
}
