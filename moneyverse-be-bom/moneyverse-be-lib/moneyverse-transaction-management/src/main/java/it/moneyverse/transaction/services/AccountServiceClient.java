package it.moneyverse.transaction.services;

import it.moneyverse.core.model.dto.AccountDto;
import java.util.UUID;

public interface AccountServiceClient {

  AccountDto getAccountById(UUID accountId);

  void checkIfAccountExists(UUID accountId);

  void checkIfAccountStillExists(UUID accountId);
}
