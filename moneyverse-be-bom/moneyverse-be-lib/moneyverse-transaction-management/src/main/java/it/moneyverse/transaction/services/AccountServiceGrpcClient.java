package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.AccountDto;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceGrpcClient implements AccountServiceClient {

  private final AccountGrpcService accountGrpcService;

  public AccountServiceGrpcClient(AccountGrpcService accountGrpcService) {
    this.accountGrpcService = accountGrpcService;
  }

  @Override
  public AccountDto getAccountById(UUID accountId) {
    return accountGrpcService
        .getAccountById(accountId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account %s does not exists".formatted(accountId)));
  }

  @Override
  public void checkIfAccountExists(UUID accountId) {
    getAccountById(accountId);
  }

  @Override
  public void checkIfAccountStillExists(UUID accountId) {
    if (accountGrpcService.getAccountById(accountId).isPresent()) {
      throw new ResourceStillExistsException(
          "Account %s still exists in the system".formatted(accountId));
    }
  }
}
