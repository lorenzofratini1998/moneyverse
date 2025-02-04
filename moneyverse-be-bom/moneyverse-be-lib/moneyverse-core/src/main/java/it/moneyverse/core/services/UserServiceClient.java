package it.moneyverse.core.services;

import java.util.UUID;

public interface UserServiceClient {

  Boolean checkIfUserExists(UUID userId);
}
