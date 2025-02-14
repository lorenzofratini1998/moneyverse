package it.moneyverse.transaction.services;

import it.moneyverse.core.model.dto.AccountDto;
import java.util.Optional;
import java.util.UUID;

public interface AccountServiceClient {

  Optional<AccountDto> getAccountById(UUID accountId);

  void checkIfAccountExists(UUID accountId);

  void checkIfAccountStillExists(UUID accountId);
}
