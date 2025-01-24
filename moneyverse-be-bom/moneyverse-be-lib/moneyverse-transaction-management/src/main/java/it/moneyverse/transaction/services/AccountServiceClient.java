package it.moneyverse.transaction.services;

import java.util.UUID;

public interface AccountServiceClient {
  Boolean checkIfAccountExists(UUID accountId);
}
